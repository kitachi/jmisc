require(["SAMPLE"], function(sample) {

    PAVO.widgets.sample = function(wid) {



        // Variables
        // wid: work id
        var rootel = "#" + wid;
        var previousOcrPage = -1;

        // Events

        var resizeTimeout;
        function addListeners() {

            if (!PAVO.util.mobile) {
                $(window).resize(function() {
                    clearTimeout(resizeTimeout);
                    resizeTimeout = setTimeout(resize, 20);
                });
                setTimeout(resize, 20);
            }

            $('#BookReader').on('click', '.BRpagediv1up', function(e) {
                if ($('#ocrview_box').first().is(":visible")) {
                    $('.ocrline').remove();
                    $('.ocredit').blur();
                    var offset = $(this).offset();
                    var point = [];
                    point.x = (e.clientX - offset.left) * br.reduce;
                    point.y = (e.clientY - offset.top) * br.reduce;
                    var pageIndex = this.id.substring(7);
                    var parentEl = this;
                    var pid = work.children.page[pageIndex].pid;
                    var page = $('#' + this.id).data();
                    // translate the point clikced on the (potentially rotated) interface into the existing coordinate system 
                    var translatedPoint = br.getRotatedPoint(point, pageIndex);
                    //traverse the ocr data to find the line clicked.
                    var results = traversePage(page, translatedPoint, isIn);
                    $.each(results, function(index) {
                        $(drawBox(this.points, pageIndex)).appendTo('#' + parentEl.id);
                        editText(this.id, this.words);
                    });
              };
            });
            
            $('.slider-action').on('click', function(e) {
                $('.ocrline').remove();
                $('.ocredit').blur();
            });

            $('#BookReader').on('getPageURI', function(event) {
                if (br.mode == 1 && ($('#pagediv' + event.index).data() != null)) {
                    $.getJSON(PAVO.config.BOOKS_OCR_SERVICE_URL.replace("${WORKPID}", event.pid), function(page) {
                        $('#pagediv' + event.index).data(page);
                    });
                    
                };
            });

            $('#BookReader').on('pageUpdated', function() {
                if (previousOcrPage != br.currentIndex()) {
                  setPageText();
                }
            });

            $('#ocrview_box').on('click', 'p', function(e) {
                $('.ocrline').remove();
                var pageEl = '#pagediv' + br.currentIndex();
                var page = $(pageEl).data();
                var id = this.id;
                if (page != null && !jQuery.isEmptyObject(page)) {
                    var results = traversePage(page, id, isId);
                    $.map(results, function(result) {
                        $(drawBox(result.points,br.currentIndex())).appendTo(pageEl);
                    });
                };
                editText(this.id, $(this).text());
            });


        }

        // Methods

        function editText(id, words) {
            var editBox = $("<textarea class='ocredit' id = " + id + "/>");
            editBox.val(words);
            $('#' + id).replaceWith(editBox);
            $(editBox).focus();
            $(editBox).blur(function() {
                $('.ocrline').remove();
                var html = $(this).val();
                var viewableText = $("<p id = " + id + ">");
                viewableText.id = id;
                $(viewableText).text(html);
                // replace out the textarea
                $(this).replaceWith(viewableText);
                // TODO: get the line in the data() object that matches our id.
                //    eg var results = traversePage(page, id, isId);
                // TODO: Then find some way to force the new words into this line. I'm assuming we'll have to look at the new bounding box and make new bounding boxes for any workds that have changed/been added.
            });
        }

        function setPageText() {
            var page = $('#pagediv' + br.currentIndex()).data();
            if (page != null && !jQuery.isEmptyObject(page)) {
                var results = traversePage(page, null, function() {
                    return true;
                });
                $('.pavo-widget #ocrview_box').text("");
                $.each(results, function(index) {
                    var line = document.createElement('p');
                    $(line).text(this.words);
                    $(line).attr('id', this.id);
                    $('.pavo-widget #ocrview_box').append(line);
                });
            } else {
                setTimeout(function() {
                    setPageText();
                }, 100);
            };
        }

        function isId(id, element) {
            return (id == element.id);
        }



        function isIn(point, element) {
            var box = element.b;
            if (point == null || box == null) {
                return true;
            }
            points = box.split(',');
            var x1 = points[0];
            var y1 = points[1];
            var x2 = points[2];
            var y2 = points[3];
            return (point.x > x1 && point.x < x2) && (point.y > y1 && point.y < y2);
        }


        //creates a box for marking where you clicked.


        function drawBox(box,index) {
          box.div = document.createElement('div');
          $(box.div).prop('className', 'BookReaderSearchHilite');
          box.l = box[0]
          box.t = box[1]
          box.r = box[2]
          box.b = box[3]
          br.styleHighliteBox1Up(box, index);
          return box.div;
        }
        //creates a Textbox for search editing.


        function drawEditBox(ocr) {
            var boxdiv = document.createElement('input');
            $(boxdiv).prop('type', 'text');
            $(boxdiv).prop('className', 'ocrEditLine');
            $(boxdiv).css({
                opacity: 1,
                'bakground color': 'white',
                position: 'absolute',
                width: (ocr.points[2] - ocr.points[0]) / br.reduce + 'px',
                height: (ocr.points[3] - ocr.points[1]) / br.reduce + 'px',
                left: (ocr.points[0]) / br.reduce + 'px',
                top: (ocr.points[3]) / br.reduce + 'px'
            });
            $(boxdiv).val(ocr.words);
            return boxdiv;
        }

        function traversePage(page, target, testFunction) {
            var results = [];
            //this traverses the regions of the page. the margins and the print space
            $.each(page, function() {
                results = results.concat(traverseZone(this, target, testFunction));
            });
            return results;
        }



        function traverseZone(zone, target, testFunction) {
            var results = [];
            if (zone.zs != null) {
                $.each(zone.zs, function(zone) {
                    results = results.concat(traverseZone(this, target, testFunction));
                });
            }
            if (zone.ps != null) {
                $.each(zone.ps, function(para) {
                    var paraResult = traversePara(this, target, testFunction);
                    if (paraResult != null) {
                        $.each(paraResult, function(index) {
                            results.push(this);
                        });
                    };
                });
            }
            return results;
        }

        function traversePara(para, target, testFunction) {
            var lineResult = [];
            if (para.ls != null) {
                $.each(para.ls, function(line) {
                    if (this != null && testFunction(target, this)) {
                        //iterate down to words level to generate string.
                        var line = [];
                        line.words = $.map(this['ws'], function(word) {
                            return word['w'];
                        }).join(' ');
                        line.points = this.b.split(',');
                        line.id = this.id;
                        lineResult.push(line);
                    };
                });
            };
            return lineResult;
        }

        function resize() {
            $(rootel).height($(window).height() - $(rootel).offset().top );
        }





        // Initialisation


        function init() {
            addListeners();
        }

        init();
    }

    PAVO.widgets.informLoaded("ocrview");
});
