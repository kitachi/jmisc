# ingest sql script template
# --- !Ups
#
start transaction;
# =======================================================================================================================
# -- Top level item
insert into dlThing(id, collectionArea, type, subType, description, link, pi, oldId)
values(<thingID>, 'nla.aus', 'work', 'book', '{"workPid":"<itemPI>","subUnitType":"","title":"<itemTitle>","creator":"<itemCreator>"}', 'VOYAGER:<itemBibId>', '<itemPI>', '<itemPI>');
# =======================================================================================================================
# -- Child element
# =======================================================================================================================
# -- Subitem
insert into dlThing(collectionArea, type, subType, description, link, pi, oldId)
values('nla.aus', 'work', 'book', '{"workPid":"<itemPI>","subUnitType":"","title":"<itemTitle>","creator":"<itemCreator>"}', 'VOYAGER:<itemBibId>', '<itemPI>', '<itemPI>');
# =======================================================================================================================
# -- Access Copy
insert into dlThing(collectionArea, type, subType, description, link, pi, oldId)
values('nla.aus', 'work', 'book', '{"workPid":"<itemPI>","subUnitType":"","title":"<itemTitle>","creator":"<itemCreator>"}', 'VOYAGER:<itemBibId>', '<itemPI>', '<itemPI>');

insert into dlThingCopy(collectionArea, type, subType, description, link, pi, oldId)
values('nla.aus', 'work', 'book', '{"workPid":"<itemPI>","subUnitType":"","title":"<itemTitle>","creator":"<itemCreator>"}', 'VOYAGER:<itemBibId>', '<itemPI>', '<itemPI>');
# =======================================================================================================================
# -- OCR JSON Copy
insert into dlThing(collectionArea, type, subType, description, link, pi, oldId)
values('nla.aus', 'work', 'book', '{"workPid":"<itemPI>","subUnitType":"","title":"<itemTitle>","creator":"<itemCreator>"}', 'VOYAGER:<itemBibId>', '<itemPI>', '<itemPI>');
# =======================================================================================================================
# -- OCR ALTO Copy
insert into dlThing(collectionArea, type, subType, description, link, pi, oldId)
values('nla.aus', 'work', 'book', '{"workPid":"<itemPI>","subUnitType":"","title":"<itemTitle>","creator":"<itemCreator>"}', 'VOYAGER:<itemBibId>', '<itemPI>', '<itemPI>');
# =======================================================================================================================
# -- Access Copy File
insert into dlThing(collectionArea, type, subType, description, link, pi, oldId)
values('nla.aus', 'work', 'book', '{"workPid":"<itemPI>","subUnitType":"","title":"<itemTitle>","creator":"<itemCreator>"}', 'VOYAGER:<itemBibId>', '<itemPI>', '<itemPI>');

insert into dlThingFile(collectionArea, type, subType, description, link, pi, oldId)
values('nla.aus', 'work', 'book', '{"workPid":"<itemPI>","subUnitType":"","title":"<itemTitle>","creator":"<itemCreator>"}', 'VOYAGER:<itemBibId>', '<itemPI>', '<itemPI>');

insert into dlFileLocation()
values();
# =======================================================================================================================
# -- OCR JSON File
insert into dlThing(collectionArea, type, subType, description, link, pi, oldId)
values('nla.aus', 'work', 'book', '{"workPid":"<itemPI>","subUnitType":"","title":"<itemTitle>","creator":"<itemCreator>"}', 'VOYAGER:<itemBibId>', '<itemPI>', '<itemPI>');

insert into dlThingFile(collectionArea, type, subType, description, link, pi, oldId)
values('nla.aus', 'work', 'book', '{"workPid":"<itemPI>","subUnitType":"","title":"<itemTitle>","creator":"<itemCreator>"}', 'VOYAGER:<itemBibId>', '<itemPI>', '<itemPI>');

insert into dlFileLocation()
values();
# =======================================================================================================================
# -- OCR ALTO File
insert into dlThing(collectionArea, type, subType, description, link, pi, oldId)
values('nla.aus', 'work', 'book', '{"workPid":"<itemPI>","subUnitType":"","title":"<itemTitle>","creator":"<itemCreator>"}', 'VOYAGER:<itemBibId>', '<itemPI>', '<itemPI>');

insert into dlThingFile(collectionArea, type, subType, description, link, pi, oldId)
values('nla.aus', 'work', 'book', '{"workPid":"<itemPI>","subUnitType":"","title":"<itemTitle>","creator":"<itemCreator>"}', 'VOYAGER:<itemBibId>', '<itemPI>', '<itemPI>');

insert into dlFileLocation()
values();
# =======================================================================================================================
# -- Relationships
# =======================================================================================================================
# -- isPartOf
insert into dlRelationship()
values();
# =======================================================================================================================
# -- isCopyOf
insert into dlRelationship()
values();
# =======================================================================================================================
# -- isFileOf
insert into dlRelationship()
values();
# =======================================================================================================================
# -- log ingest job status
insert into dlIngestStatus(jobId, status, statusDescription)
values(<jobId>, 1, 'Ingested metadata.');

commit;

# --- !Downs
