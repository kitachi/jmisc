

package ismn;

import nla.*;
import nla.map.*;
import nla.util.*;
import java.util.*;
import nla.util.Chunk;
import java.text.SimpleDateFormat;
import java.sql.Connection;

// Import email classes and utility class.
import nla.util.mail.Email;
import nla.util.mail.EmailTools;
import nla.util.mail.WMEmail;
import javax.mail.MessagingException;



public class AllocateISMN extends UpdateAction {
  public static final String _defaultStatus  = "allocateismns";
  public static final String _name           = "AllocateISMN";
  public static final String _title          = "ISMN Agency Administration System: AllocateISMN";
  public static final Hashtable _statusCodes = new Hashtable();
  public static final Hashtable _sessionSet  = new Hashtable();
  public static Hashtable ismnSuperSizeList  = new Hashtable();

  String message      = "";
  String mess         = "";
  String pubid        = "";
  String serviceid    = "";
  String identifierid = "";
  int noOfChunks      = 9;
  int chunkSize       = 50;

 

  static {
    _statusCodes.put("allocateismns",    "AllocateISMN: Allocate ISMNs to an identifier");
    _statusCodes.put("ismnerror",        "AllocateISMN: error. Action exception.");
    _statusCodes.put("allocatedISMNmail","AllocateISMN: mail: Allocated ISMNs.");
    _statusCodes.put("index",           "AllocateISMN:: Home");


    _sessionSet.put("identifier",        "IsmnIdentifier");
    _sessionSet.put("org",               "Organisation");
    _sessionSet.put("service",           "Service");
    _sessionSet.put("ismninfo",          "IsmnInformation");
    _sessionSet.put("unallocismns",      "");
    _sessionSet.put("allocismns",        "");
    _sessionSet.put("ismnworkcodelkup",  "");
    _sessionSet.put("chunkarray",        "");
    _sessionSet.put("defaultChunk",      "");
    _sessionSet.put("prevChunk",         "");
    _sessionSet.put("currentChunk",      "");
    _sessionSet.put("numOfDigits",       "");
    _sessionSet.put("superSize",         "");
    _sessionSet.put("newAllocs",         "");
    _sessionSet.put("nextThousChunk",    "");
    _sessionSet.put("prevThousChunk",    "");



    ismnSuperSizeList.put("one",   "10");
    ismnSuperSizeList.put("two",   "100");
    ismnSuperSizeList.put("three", "1000");
    ismnSuperSizeList.put("four",  "10000");
    ismnSuperSizeList.put("five",  "100000");

    }

  public AllocateISMN (Hashtable args, QueryStore qs,
		                        Properties prop, Hashtable sess) {
	  super(args, qs, prop, sess);
  }

  public AllocateISMN (Hashtable args, QueryStore qs, Properties prop) {
        super(args, qs, prop);
  }

  public AllocateISMN (QueryStore qs) { super(qs); }

  public boolean gotResources() {
    return true;
  }

  public void initSession() {


    // we get identifierid from search query of identifier(if we try to get org and other objects it will slow down the query)
    identifierid = (String)_args.get("identifierid");
    logit("identifierid     " +identifierid);

    IsmnIdentifier identifier = new IsmnIdentifier(identifierid, _qs);

    try {
        identifier.load();
        _sess.put("identifier", identifier);
        logit("identifier " +identifier);

        String numOfDigits = (String) identifier.getVal("NUM_OF_DIGITS");
        _sess.put("numOfDigits", numOfDigits);

        String ismnSizeKey = findIsmnSizeKey (8-Integer.parseInt(numOfDigits));
        logit ("ismnSizeKey:::::::::"+ismnSizeKey);

        String superSize = (String) ismnSuperSizeList.get(ismnSizeKey);
        _sess.put("superSize", superSize);
        logit ("superSize:::::::::"+superSize);

        IsmnInformation ismninfo = (IsmnInformation) identifier.relation("ISMNINFORMATION");
        _sess.put("ismninfo", ismninfo);
        logit("ismninfo " +ismninfo);

        Service service = (Service) ismninfo.relation("SERVICE");
        _sess.put("service", service);
        serviceid = service.key();
        logit("service " +service);

        Organisation org = (Organisation) service.relation("ORGANISATION");
        _sess.put("org", org);
        pubid = org.key();
        logit("org " +org);

        ObjectSet allocismns = identifier.relatives(true,"ISMNS");
        _sess.put("allocismns", allocismns);

        ObjectSet unallocismns = new ObjectSet (_qs);
        unallocismns.setClassName("IsmnISMN");
        _sess.put( "unallocismns", unallocismns);

        Chunk[] chunkArray = new Chunk[noOfChunks+1];
        _sess.put( "chunkarray", chunkArray);

        _sess.put("ismnworkcodelkup", _qs.getRegistry("IsmnWorkCodeLookup"));

        Chunk defaultChunk = new Chunk();
        _sess.put("defaultChunk", defaultChunk);

        Chunk prevChunk = new Chunk();
        _sess.put("prevChunk", prevChunk);

        Chunk currentChunk = new Chunk();
        _sess.put("currentChunk", currentChunk);

        Chunk nextThousChunk = new Chunk();
        _sess.put("nextThousChunk", nextThousChunk);

        Chunk prevThousChunk = new Chunk();
        _sess.put("prevThousChunk", prevThousChunk);

        ObjectSet newAllocs = new ObjectSet (_qs);
       // newAllocs.setClassName("IsmnISMN");
        _sess.put( "newAllocs", newAllocs);

        } catch (RecordNotFoundException rnfe) {
          message = rnfe.getMessage();
        }

  } //initSession()



  public Hashtable process() throws ActionException {

    _status = _defaultStatus;
    
    // check whether a new chunk size has been selected for allocating the ISMNs
    if (_args.get("newChunkSize") != null) {
    	chunkSize = new Integer((String) _args.get("newChunkSize")).intValue();
        Chunk defaultChunk   = (Chunk) _sess.get("defaultChunk");
        defaultChunk.setLast(defaultChunk.getFirst() + chunkSize - 1);
        defaultChunk.setLabel(defaultChunk.getFirst() + "-" + defaultChunk.getLast());
        Chunk currentChunk   = (Chunk) _sess.get("currentChunk");
        currentChunk.setLast(currentChunk.getFirst() + chunkSize - 1);
        currentChunk.setLabel(currentChunk.getFirst() + "-" + currentChunk.getLast());
        
        Chunk prevChunk      = (Chunk) _sess.get("prevChunk");
        prevChunk.setLast(prevChunk.getFirst() + chunkSize - 1);
        prevChunk.setLabel(prevChunk.getFirst() + "-" + prevChunk.getLast());
        
        Chunk nextThousChunk = (Chunk) _sess.get("nextThousChunk");
        nextThousChunk.setLast(nextThousChunk.getFirst() + chunkSize - 1);
        nextThousChunk.setLabel(nextThousChunk.getFirst() + "-" + nextThousChunk.getLast());
        
        Chunk prevThousChunk = (Chunk) _sess.get("prevThousChunk");
        prevThousChunk.setLast(prevThousChunk.getFirst() + chunkSize - 1);
        prevThousChunk.setLabel(prevThousChunk.getFirst() + "-" + prevThousChunk.getLast());
        
        _sess.put("defaultChunk", defaultChunk);
        _sess.put("currentChunk", currentChunk);
        _sess.put("prevChunk", prevChunk);
        _sess.put("nextThousChunk", nextThousChunk);
        _sess.put("prevThousChunk", prevThousChunk);
        
    	createChunks();
    }
        
    replySession();

    String mode = getMode();

    // cancel (do not assign- will take you to search form again)
    if (mode.equals("cancel")) {
        _status = "index";
        clearSession();
        initSession();
        replySession();
      }

    if (mode.equals("generateISMN") || mode.equals("reset") ) {   //addding reset here as I need to do DoChunks("default")
    //done before generateISMN as I need to add unallocIsmns to session and do replySession to put info in the template
    // also required to do initsession for next identifier
        clearSession();
        initSession();
        _status = doChunks("default");
        replySession();
      }
    if (mode.equals("doChunk")){
        _status = doChunks((String) _args.get ("mode!doChunk"));

        replySession();
      }

    if (mode.equals("allocCheckedIsmn")) {
        _status = allocCheckedIsmn();
       // clearSession();
       // initSession();
        replySession();
      }

    //update  last ismn alloc date in "UpdatePublisher" Action and display iddetailsform in UpdateIDentifierDetails usecase

    _reply.put("pubid", pubid);
    _reply.put("serviceid", serviceid);
    _reply.put("message", message);
    _reply.put("identifierid", identifierid);
    _reply.put("status", _status);
    _reply.put("newchunksize", chunkSize);

    if (((String) _reply.get("status")).equals("updateid")) {
      Hashtable args = new Hashtable(4);
      Organisation org = (Organisation) _sess.get("org");
      Service service  = (Service) _sess.get("service");
      IsmnIdentifier identid =  (IsmnIdentifier) _sess.get("identifier");
      args.put("pubid", org.key());
      args.put("mode", "showdetails");    //"updateIsmnAlloc"
      args.put("serviceid", service.key());
      args.put("identifierid", identid.key());

      args.put("message", "Updating LastIsmnAllocationDate for Publisher with:<BR> Id: "+ pubid);
      
      // removed the routing to Update Identifier Details page if the status is updateid
      // instead, it stays on the Allocate ISMN page
      // sz  22/05/2012
      
      Object unallocismns = _reply.get("unallocismns");
      _status = doChunks("default");

      // _sess.put("unallocismns", unallocismns);
      _reply.put("status", _status);
      // _reply.put("unallocismns", unallocismns);
      replySession();
    }

    return _reply;

  }  //process

  private String generateISMN() {
    message = "";

    IsmnIdentifier identid =  (IsmnIdentifier) _sess.get("identifier");

    int numDigits = Integer.parseInt((String)_sess.get("numOfDigits"));
     logit ("numDigits(generateISMN) :::::::::::::" + numDigits);

    ObjectSet unallocIsmns = new ObjectSet (_qs);

    int superSize = Integer.parseInt((String)_sess.get("superSize"));
    logit ("superSize(generateISMN):::::::::"+superSize);

    int unallocnum = unallocIsmns.number();
    logit ("unallocnum???????"+unallocnum);

    ObjectSet allocIsmnSet = (ObjectSet) _sess.get("allocismns");  // it will have atleast one as we are forceloading
    String testkey = allocIsmnSet.firstMember().key();
    logit ("allocIsmnSet.firstMember().key() :::::   " + allocIsmnSet.firstMember().key());
    ///////vit 18 Dec 2007 testing allocismns
    Enumeration en = allocIsmnSet.members();
    logit("allocIsmnSet.members():::::   ");
    while (en.hasMoreElements()){
    	IsmnISMN ismn = (IsmnISMN)en.nextElement();
    	logit("ismn:"+ ismn.get("ISMN_NUM"));
    }
    ///////vit 18 Dec 2007 testing allocismns
    
    // get the new currentChunk again (if superSize = 10)
    Chunk currentChunk = (Chunk) _sess.get("currentChunk");

    Hashtable flds = new Hashtable(3);
    flds.put("IDENTIFIER_ID"      , identid.key());
    flds.put("ISMN_STATUS"        , "Y");
    //vit 10-12-2007 13digitISMN change - always use 9790 instead of M for new unallocated ismns  
   // flds.put("ISMN_PREFIX"        , "M");  
    flds.put("ISMN_PREFIX"        , "9790");
    flds.put("ISMN_NUM_ELEM_ONE"  , (String) identid.get("IDENTIFIER_NUM"));


    if (testkey != null && !testkey.equals("")) {

        int fismno = currentChunk.getFirst();
        int lismno = currentChunk.getLast();

        int k = 0;
        while ((k < chunkSize) && ( fismno <= lismno)){

          String prefixedNum = prefixZeroes (fismno, 8-numDigits);
          
          // check for an existing ISMN with 9790 prefix          
          String completeIsmn = completeISMN (prefixedNum, "9790");
          String checkSum     = addChecksum(completeIsmn);
          String checkSumIsmn = completeIsmn.concat(checkSum);
          
          logit ("check for an existing ISMN with 9790 prefix in allocIsmnSet::"+checkSumIsmn);
          
          IsmnISMN ismn1 = (IsmnISMN) allocIsmnSet.memberWhere ("ISMN_NUM", checkSumIsmn);
          
          // check for an existing ISMN with M prefix
          IsmnISMN ismn2 = null;
          if (ismn1 == null) {         
            String completeIsmn1 = completeISMN (prefixedNum, "M");
            String checkSum1     = addChecksum(completeIsmn1);
            String checkSumIsmn1 = completeIsmn1.concat(checkSum1);
            ismn2 = (IsmnISMN) allocIsmnSet.memberWhere ("ISMN_NUM", checkSumIsmn1);
            logit ("check for an existing ISMN with M prefix in allocIsmnSet::"+checkSumIsmn1);
          }
          
          // nothing exists hence an unallocated one
         if (ismn1 == null && ismn2 == null) {
              IsmnISMN ismnval = new IsmnISMN (_qs);
              ismnval.setFields(flds);
              ismnval.set("ISMN_NUM_ELEM_TWO", prefixedNum);
              ismnval.set("ISMN_NUM", checkSumIsmn);
              ismnval.set("ISMN_NUM_ELEM_THREE", checkSum);

              unallocIsmns.addAt(ismnval,k);
              fismno++;
              k++;
          }
          else   fismno++;

        } //while
    }

    else {
      int upperVal = chunkSize;

      if (superSize < chunkSize)
         upperVal = superSize;

      for (int i = 0; i < upperVal; i++){
        IsmnISMN ismn = new IsmnISMN (_qs);
         unallocIsmns.addAt(ismn,i);
      }
      int no = 0;

      int ismno = currentChunk.getFirst();
      while (no < upperVal){
        IsmnISMN ismn = (IsmnISMN) unallocIsmns.elementAt(no);

        String prefixedNum = prefixZeroes (ismno, 8-numDigits);
        String completeIsmn = completeISMN(prefixedNum, "9790");
        String checkSum     = addChecksum(completeIsmn);
        String checkSumIsmn = completeIsmn.concat(checkSum);
        //logit ("completeIsmn:::::" + completeIsmn);

        ismn.setFields(flds);
        ismn.set("ISMN_NUM_ELEM_TWO", prefixedNum);
        ismn.set("ISMN_NUM", checkSumIsmn);
        ismn.set("ISMN_NUM_ELEM_THREE", checkSum);

        logit ("unallocIsmns:::::" + (String) ismn.getVal("ISMN_NUM"));

        no++;

        ismno++;

      }
    }
        
    _sess.put("unallocismns", unallocIsmns);

    return "allocateismns";

  } //generateIsmn

  private boolean updateNewAllocs() {

    boolean error = false;

    ObjectSet newAllocs = (ObjectSet) _sess.get("newAllocs");
    ObjectSet unallocismns = (ObjectSet) _sess.get("unallocismns");
    Enumeration unallocEnum = unallocismns.objects();

    while (unallocEnum.hasMoreElements()){   //2
        IsmnISMN ismn = (IsmnISMN) unallocEnum.nextElement();
        //logit ("ismn ?????????:" + ismn);
        String ISMNnum = (String) _args.get((String)ismn.getVal("ISMN_NUM"));
        StringTokenizer stokens = new StringTokenizer( ISMNnum, "|");
        int tokenCount = stokens.countTokens();
        //logit ("tokenCount:::::" + tokenCount);

        if (tokenCount >= 2) {  //3
          String onTest =  stokens.nextToken();
          if (onTest.equals("on")) { //3a
              //logit ("onTest:::::" + onTest);
            try {   //4
              ismn.checkSet("WORK_CODE",stokens.nextToken());
              ismn.checkSet("ISMN_STATUS","Y");


            }catch (InvalidValueException inrex) {
                 mess = inrex.getMessage();
                 return error = true;
            }  //4
            if (tokenCount == 3) {  //5
              String amicus = stokens.nextToken();
              ismn.set("AMICUS_NUM", amicus);
              logit ("tokenCount3:::::" + amicus);
            }   //5
            if (tokenCount == 4) {  //6
              String amicus = stokens.nextToken();
              ismn.set("AMICUS_NUM", amicus);
              logit ("tokenCount3:::::" + amicus);
              String title = stokens.nextToken();
              ismn.set("TITLE", title);
              logit ("tokenCount4:::::" + title);
            }   //6
             // moved here on 11 June.To add ismn only if tokenCount is 2 or more and equals "on" is true
            newAllocs.add(ismn);
          } //3a
        } //3
      } //2

      _sess.put("newAllocs", newAllocs);

      return error;

  }  //updateNewAllocs

  /*private void removeDuplicateISMNs() {
    ObjectSet newAllocs = (ObjectSet) _sess.get("newAllocs");
    ObjectSet allocs = new ObjectSet (_qs);

    newAllocs.sort("ISMN_NUM");
    // first element
    IsmnISMN ismnf  = (IsmnISMN) newAllocs.elementAt(0);
    String tester   = (String) ismnf.getVal("ISMN_NUM");
    allocs.add (ismnf);

    int i = 1;
    while (i < newAllocs.number())  {
      IsmnISMN ismn  = (IsmnISMN) newAllocs.elementAt(i);
      String ismnNum = (String) ismn.getVal("ISMN_NUM");
      logit ("ismnNum>>>>>>>"+ismnNum);

      if (tester.equals(ismnNum)) {
        logit ("ismnNumDuplicate>>>>>>>"+ismn.getVal("ISMN_NUM"));
        logit ("ismnNumDuplicate>>>>>>>"+ismn.getVal("WORK_CODE"));
        logit ("ismnNumDuplicate>>>>>>>"+ismn.getVal("COMMENTS"));
        i++;
      }
      else {
        allocs.add (ismn);
        tester = ismnNum;
        i++;
      }
    }

    _sess.put("newAllocs", allocs);

  } //removeDuplicateISMNs*/

  // Picking the last ismn value added for duplicates as user changes values
  private void removeDuplicateISMNs() {
    ObjectSet newAllocs = (ObjectSet) _sess.get("newAllocs");
    ObjectSet allocs = new ObjectSet (_qs);

    //sorts by putting the last value added on top if duplicates
    newAllocs.sort("ISMN_NUM");

    //add the first ismn of newAllocs always
    IsmnISMN ismnF = (IsmnISMN) newAllocs.elementAt(0);
    allocs.add(ismnF);
    logit ("ismnNumDuplicateFirstAdded>>>>>>>"+ismnF.getVal("ISMN_NUM"));
    logit ("ismnNumDuplicateFirstAdded>>>>>>>"+ismnF.getVal("WORK_CODE"));
    logit ("ismnNumDuplicateFirstAdded>>>>>>>"+ismnF.getVal("AMICUS_NUM"));

    for (int i = 1; i < newAllocs.number(); i++) {
        IsmnISMN ismnP    = (IsmnISMN) newAllocs.elementAt(i-1);
        IsmnISMN ismnN    = (IsmnISMN) newAllocs.elementAt(i);
        String   ismnNumP = (String)   ismnP.getVal("ISMN_NUM");
        String   ismnNumN = (String)   ismnN.getVal("ISMN_NUM");
        logit ("ismnNumDuplicate>>>>>>>"+ismnP.getVal("ISMN_NUM"));
        logit ("ismnNumDuplicate>>>>>>>"+ismnP.getVal("WORK_CODE"));
        logit ("ismnNumDuplicate>>>>>>>"+ismnP.getVal("AMICUS_NUM"));
        logit ("ismnNumDuplicate>>>>>>>"+ismnN.getVal("ISMN_NUM"));
        logit ("ismnNumDuplicate>>>>>>>"+ismnN.getVal("WORK_CODE"));
        logit ("ismnNumDuplicate>>>>>>>"+ismnN.getVal("AMICUS_NUM"));
        if (!ismnNumP.equalsIgnoreCase(ismnNumN)) {
            allocs.add(ismnN);
            logit ("ismnNumDuplicateAdded>>>>>>>"+ismnN.getVal("ISMN_NUM"));
            logit ("ismnNumDuplicateAdded>>>>>>>"+ismnN.getVal("WORK_CODE"));
            logit ("ismnNumDuplicateAdded>>>>>>>"+ismnN.getVal("AMICUS_NUM"));
        }
    }

    _sess.put("newAllocs", allocs);

  } //removeDuplicateISMNs
  
  private String allocCheckedIsmn(){

  boolean err = false; //30/05/02
  // calling updateNewAllocs here to get the checked values from current chunk also so that newAllocs os contains all new AllocISMns
  boolean error = updateNewAllocs();

  // create new hashtable to send mail
  Hashtable argsData = new Hashtable(5);

  ObjectSet newAllocstest  = (ObjectSet) _sess.get("newAllocs");

  //30/05/02
  if (error && (newAllocstest.number() == 0)){
    err =  true;
    message = mess;
  }

  if ((newAllocstest.number() == 0)&& !error)  {
    err = true;
    message = "Please select Ismns for allocation.";
  }

  if (err) return "ismnerror";

  else {

    // remove all duplicate ISMNs from newAllocOs if entered by mistake before commiting
    removeDuplicateISMNs();

    ObjectSet unallocismns    = (ObjectSet) _sess.get("unallocismns");
    Organisation org          = (Organisation) _sess.get("org");
    IsmnIdentifier identifier = (IsmnIdentifier) _sess.get("identifier");
    IsmnInformation ismninfo  = (IsmnInformation) _sess.get("ismninfo");
    ObjectSet newAllocsOs     = (ObjectSet) _sess.get("newAllocs");
    String numAllocIsmns      = (String) identifier.getVal("ALLOC_ISMN_NUM");
    int numAlloc              = Integer.parseInt(numAllocIsmns);
    String numUnAllocIsmns    = (String) identifier.getVal("UNALLOC_ISMN_NUM");
    int numUnAlloc            = Integer.parseInt(numUnAllocIsmns);

    argsData.put("pubName",(String)org.getVal("NAME"));
    argsData.put("identifier",(String)identifier.getVal("IDENTIFIER_NUM"));

   try{

    //vit  Enumeration unallocEnum = unallocismns.objects();
     java.sql.Connection con = null;
     try {  //1

      con = _qs.getConnection(true);

      message = "Allocated following ISMN(s) to Identifier "+ (String)identifier.getVal("IDENTIFIER_NUM")+ " :";

      // update last_ismn_Alloc_date in ismnInformation
      SimpleDateFormat formatter = new SimpleDateFormat ("dd MMMMMMMMMM yyyy");
      Date date = new Date();
      String dateString = formatter.format(date);
      ismninfo.set("LAST_ISMN_ALLOC_DATE",dateString);
      // sz: 17.05.2013
      String failedISMNAlloc = "";

       //commit (update) all the elements from previous chunks (already updated by updateNewAllocs())
       Enumeration newAllocs = newAllocsOs.objects();
       while (newAllocs.hasMoreElements()){
         IsmnISMN ismn = (IsmnISMN) newAllocs.nextElement();
         
         // sz: check the ismn already exist in the db,
         //     if not, add the ismn record in
         String ismnNum = (String) ismn.getVal("ISMN_NUM");
         try {
             identifier.insertRelative(con,"ISMNS",ismn);
             message = message + (String) ismn.getVal("ISMN_NUM");
         } catch (QStoreException ex) {
        	 // logit("the ismn might be already allocated, in most cases the exception can be ignored.");
		 // sz: 17.05.2013 TODO: in here
                 // TODO: newAllocsOs.remove(ismn);
                 // TODO: if (!failedISMNAlloc.isEmpty()) failedISMNAlloc += ", ";
                 // TODO: failedISMNAlloc += ismn.getVal("ISMN_NUM");
         }
                  
         numAlloc++;
         identifier.set("ALLOC_ISMN_NUM", Integer.toString(numAlloc));
         numUnAlloc--;
         identifier.set("UNALLOC_ISMN_NUM", Integer.toString(numUnAlloc));
       }      
       
       // to change status from current to previous when all ISMNs are allocated for an identifier
       if (numUnAlloc == 0) identifier.set("ID_STATUS","previous");

      identifier.save(con);
      ismninfo.save(con);

      con.commit();

      message = message + "Allocated ISMNs successfully.";

      } catch (Exception ex) {
	        logit("commitSession: " + ex, 3);
          try { con.rollback();
          } catch (Exception ex2) {}
	        throw new ActionException(ex.getMessage());
      } finally {
          if (con != null)
            _qs.putConnection(con);
      }

      // sz: 17.05.2013
      if (newAllocsOs == null || newAllocsOs.isEmpty()) throw new ActionException("unable to allocate " + failedISMNAlloc + ".");

      //add newlyAllocatedISMNs
      argsData.put("allocIsmns", newAllocsOs);
      argsData.put("message", "Successfully allocated the following ISMN(s) to Identifier " + (String)identifier.getVal("IDENTIFIER_NUM")+ " :");

      //sendMail when allocated ismns successfully
      sendMail (argsData);

      _reply.put("unallocismns", unallocismns);
      return "updateid";       // ismn and identifier updated successfully. Also updated lastismnalloc in publisher and display updated identifier with allocated ismns

    }catch (ActionException ae) {
          message = ae.getMessage();
	        return "ismnerror";
    }
   }//else before try

  } //allocCheckedIsmn



  private String findIsmnSizeKey (int digits){
    String val = "";
    switch (digits){
      case 1: val = "one";
                   break;
      case 2: val = "two";
                   break;
      case 3: val = "three";
                   break;
      case 4: val = "four";
                   break;
      case 5: val = "five";
                   break;
    }
    return val;
  } //findIsmnSizeKey


  private String prefixZeroes(int ismn, int numDigits) {      // numDigits = (8 - Identifier.NUM_OF_DIGITS)

    String paddedIsmn = "";
    int numOfZeroes = 0;

    String ismnStr = Integer.toString (ismn);
    int ismnDigits = ismnStr.length();

    if (ismnDigits < numDigits) {
      for (int i = 0; i < (numDigits-ismnDigits); i++ ){
        paddedIsmn = paddedIsmn.concat("0");
      }
    }
    paddedIsmn = paddedIsmn.concat(ismnStr);

    return paddedIsmn;

  } //prefixZero

  private String completeISMN(String ismn, String prefix){
    IsmnIdentifier identid =  (IsmnIdentifier) _sess.get("identifier");
    String completeIsmn = "";

    //completeIsmn = completeIsmn.concat("M"); vit 10-12-2007 13digitISMN change - can take M or 9790 as prefix
    completeIsmn = completeIsmn.concat(prefix);
    completeIsmn= completeIsmn.concat((String)identid.getVal("IDENTIFIER_NUM"));
    completeIsmn= completeIsmn.concat(ismn);
    //completeIsmn = completeIsmn.concat(addChecksum(completeIsmn));
    return completeIsmn;
  }


  private String doChunks (String chunkLabel) {

    String stat = "";
    Chunk chunk;

    boolean error = updateNewAllocs();

    ObjectSet newAllocs = (ObjectSet) _sess.get("newAllocs");
    if (newAllocs.number() > 1 )
    removeDuplicateISMNs(); // when moving between chunks remove duplicates and hold the latest value


    if (error) return "ismnerror";

    else{

    setCurrentChunk (chunkLabel);   // this is being called here to set current Chunk

    Chunk defaultChunk   = (Chunk) _sess.get("defaultChunk");
    Chunk prevChunk      = (Chunk) _sess.get("prevChunk");
    Chunk currentChunk   = (Chunk) _sess.get("currentChunk");
    Chunk nextThousChunk = (Chunk) _sess.get("nextThousChunk");
    Chunk prevThousChunk = (Chunk) _sess.get("prevThousChunk");

    logit ("chunkLabel ::::" + chunkLabel);
    int superSize = Integer.parseInt((String)_sess.get("superSize"));     //11/10

    if (chunkLabel.equals("default")) {
      if (superSize == 10) {
        defaultChunk.setAttribs (0, superSize-1);
        setCurrentChunk ("superSize");
      }
      else {
        defaultChunk.setAttribs (0, chunkSize-1);
        if (superSize > 1000)                         //11/10
        nextThousChunk.setAttribs (1000, 1019);
      }
    }
    //create chunks for next or previous
    else if (chunkLabel.equals("next")) {
      prevChunk.setAttribs (defaultChunk.getFirst(), defaultChunk.getLast());
      // currentChunk has values of "next" btn (ie beginning of next ChunkSet)
      defaultChunk.setAttribs (currentChunk.getFirst(), currentChunk.getLast());
      //for ismnsNo >1000
      if (superSize > 1000) {
        //when next goes beyond the threshold of 1000's then need to change value of nextThousChunk and prevThousChunk
        if (defaultChunk.getFirst() == nextThousChunk.getFirst()) {
          if (!((nextThousChunk.getFirst() - 1000) < 0)) {
            prevThousChunk.setAttribs (nextThousChunk.getFirst()-1000, nextThousChunk.getLast()-1000);
          } else prevThousChunk.setAttribs (0,0);
          nextThousChunk.setAttribs (nextThousChunk.getFirst()+1000, nextThousChunk.getLast()+1000);
        }
      }
    }
    else if (chunkLabel.equals("previous")) {
      defaultChunk.setAttribs (prevChunk.getFirst(), prevChunk.getLast());
      if (prevChunk.getFirst()>0){
      prevChunk.setAttribs (prevChunk.getFirst()-100, prevChunk.getLast()-100);
      }
      //for ismnsNo >1000
      if (superSize > 1000) {
        //when previous goes beyond the threshold of 1000's then need to change value of nextThousChunk and prevThousChunk
        if (defaultChunk.getFirst() < nextThousChunk.getFirst()-1000) {
          nextThousChunk.setAttribs (nextThousChunk.getFirst()-1000, nextThousChunk.getLast()-1000);
          if (prevThousChunk.getFirst()>0) {
            prevThousChunk.setAttribs (prevThousChunk.getFirst()-1000, prevThousChunk.getLast()-1000);
          } else prevThousChunk.setAttribs (0,0);
        }
      }
    }
    else if (chunkLabel.equals("next1000")) {
      prevThousChunk.setAttribs (nextThousChunk.getFirst()-1000, nextThousChunk.getLast()-1000);
      defaultChunk.setAttribs (nextThousChunk.getFirst(), nextThousChunk.getLast());
      prevChunk.setAttribs (nextThousChunk.getFirst()-100, nextThousChunk.getLast()-100);
      nextThousChunk.setAttribs (nextThousChunk.getFirst()+1000, nextThousChunk.getLast()+1000);
    }

    else if (chunkLabel.equals("prev1000")) {
      defaultChunk.setAttribs (prevThousChunk.getFirst(), prevThousChunk.getLast());
      if (defaultChunk.getFirst() > 0) {
        prevChunk.setAttribs (defaultChunk.getFirst()-100, defaultChunk.getLast()-100);
      } else prevChunk.setAttribs (0,0);
      nextThousChunk.setAttribs (defaultChunk.getFirst()+1000, defaultChunk.getLast()+1000);

      if (prevThousChunk.getFirst() > 0) {
        prevThousChunk.setAttribs (prevThousChunk.getFirst()-1000, prevThousChunk.getLast()-1000);
      } else prevThousChunk.setAttribs (0,0);

    }
   // in all cases do this always even when the chunks do not have the above conditions
   stat = generateISMN();          //10/10test
   createChunks();                 //10/10test

    logit (" after doChunks()++++++++++++++++++++++++++++++++++++++++++");
    logit("defaultChunk:::::::"+ defaultChunk.getLabel());
    logit("prevChunk:::::::"+ prevChunk.getLabel());
    logit("nextThousChunk:::::::"+ nextThousChunk.getLabel());
    logit("prevThousChunk:::::::"+ prevThousChunk.getLabel());
    logit ("++++++++++++++++++++++++++++++++++++++++++");

    _sess.put("defaultChunk",defaultChunk);
    _sess.put("prevChunk",prevChunk);
    _sess.put("nextThousChunk",nextThousChunk);//11/10
    _sess.put("prevThousChunk",prevThousChunk);//11/10

    return stat;

    }//else

  } //doChunks

  private void createChunks () {

    //clear chunkArray everytime
    initChunks();

    boolean setPrev   = false;
    boolean setThous  = false;

    Chunk defaultChunk   = (Chunk) _sess.get("defaultChunk");
    Chunk prevChunk      = (Chunk) _sess.get("prevChunk");
    Chunk nextThousChunk = (Chunk) _sess.get("nextThousChunk");
    Chunk prevThousChunk = (Chunk) _sess.get("prevThousChunk");

   // int numDigits = Integer.parseInt((String)_sess.get("numOfDigits")); 10/10test
   // logit (" numDigits(createChunks) :::::::::::::" +  numDigits);     10/10test

    int superSize = Integer.parseInt((String)_sess.get("superSize"));
  //  logit ("superSize(createChunks):::::::::"+superSize);

    Chunk[] chunkArray = (Chunk[]) _sess.get("chunkarray");

    int first = defaultChunk.getFirst();
    int last  = defaultChunk.getLast();

    if (superSize > 10) {

      if (first >= 100){
        Chunk chunk = new Chunk (prevChunk.getFirst(), prevChunk.getLast(), "previous");
        chunkArray [0] =  chunk;
        setPrev = true;
      }
      if (superSize > 1000) {      // 11/10
          setThous = true;
      }
      //logit("setPrev:::::"+setPrev);
      int i = 0, num = 0;

      if(setPrev){         //when
        i = 1;
       //num = noOfChunks - 1;
       num = noOfChunks - 3;
      }
      else {
        //num = noOfChunks - 2;
        num = noOfChunks - 4;
      }

      // logit("i::::"+ i);
       logit("num::::"+ num);

      for (; i < num; i++){
         Chunk chunk = new Chunk( first, last);
         first = first + chunkSize;
         last  = last + chunkSize;
         chunkArray[i] = chunk;
         logit ("chunk ::::" + chunk.getLabel());
      }
      if (last < superSize) {
        Chunk chunk = new Chunk ( first, last, "next");
        chunkArray [num] = chunk;
        logit ("chunk ::::" + chunk.getLabel()+chunk.getFirst() + chunk.getLast());
      }

      if(setThous && nextThousChunk.getFirst() > 1000) {       // 11/10
       Chunk chunk = new Chunk ( prevThousChunk.getFirst(), prevThousChunk.getLast(), "prev1000");
       chunkArray [num +1] = chunk;
       logit ("chunk ::::" + chunk.getLabel()+":"+chunk.getFirst()+":" + chunk.getLast());
      }

      if(setThous && nextThousChunk.getLast()< superSize) {       // 11/10
       Chunk chunk = new Chunk ( nextThousChunk.getFirst(), nextThousChunk.getLast(), "next1000");
       if  (nextThousChunk.getFirst() > 1000)  chunkArray [num +2] = chunk;
       else chunkArray [num +1] = chunk;
       logit ("chunk ::::" + chunk.getLabel()+":"+chunk.getFirst()+":" + chunk.getLast());
      }
    } //if (!(superSize == 10))

     _sess.put ("chunkarray", chunkArray);

  } //createChunks

  private void setCurrentChunk (String chunkLabel) {

	// TODO: loggit @ debug level (i.e. 5) tomorrow to debug 
	// ie. logit(msg, 5);
	  
    Chunk chunk;
    boolean found = false;
    Chunk currentChunk = (Chunk) _sess.get("currentChunk");
    int superSize = Integer.parseInt((String)_sess.get("superSize"));

    if (chunkLabel.equals("default")) {
      currentChunk.setAttribs (0, chunkSize-1);
      }
    else if (chunkLabel.equals("superSize")){
      currentChunk.setAttribs (0, superSize-1);
      }
    else {
       Chunk[] chunkArray = (Chunk[]) _sess.get("chunkarray");
      int i = 0;
      while (!found && (i < noOfChunks)){
        chunk = chunkArray[i];
        if ((chunk.getLabel()).equals(chunkLabel)){
          found = true;
          currentChunk.setAttribs (chunk.getFirst(), chunk.getLast());
        }
        i++;
      }
    }
      logit("currentChunk:::::::"+ currentChunk.getLabel());
      _sess.put("currentChunk",currentChunk);


  }  //setCurrentChunk

  private void initChunks(){
    Chunk[] chunkArray = (Chunk[]) _sess.get("chunkarray");
    for(int i = 0; i < noOfChunks; i++){
      chunkArray[i] = null;
    }

  } //initChunks

  private String addChecksum (String ismn) {

	    int sum = 0;
	    int icheck = 1;
	    int i = 0;
      if (ismn.startsWith("M")){
      	i=1;
      	sum = 3*3;
      	logit ("prefix=M:::::sum=" +sum);
      }else {
      	i = 4;
      	sum = 39;
      	logit ("prefix=9790:::::sum=" +sum);
      }
	    
	    //vit 10-12-2007 13DigitISMN instead of 10 digit
	    for (; i<ismn.length(); i++) {
	      String digitS = ismn.substring(i, i+1);
	      int digit = Integer.parseInt(digitS);

	         if(icheck == 1) {
	            sum = sum + digit;
	            icheck = 0;
	            //logit ("value=1:::::sum + digit:::" +sum);
	         }
	         else {
	            sum = sum + digit*3;
	            icheck = 1;
	            //logit ("value=3:::::sum + digit*3:::" +sum);
	         }
	    } //for

	    if ( sum % 10 == 0)  {
	    	logit ("checksum=0");
	    	return "0";
	    }
	    else {
	      int next = sum + 1;  

	     // logit ("next =sum+1))))))))"+next);
	     // logit ("sum))))))))"+sum);

	      while (next % 10 != 0) {
	        next++;
	       // logit ("(next)"+next);
	      }
	      logit ("((((((((checksum=(next-sum))))))))"+Integer.toString(next - sum));
	      
	      return (Integer.toString(next - sum));
	    }  //else
	  }  //addChecksum

   private String sendMail (Hashtable argsData) {
    logit ("INside SendMail %%%%%%%%%%%");

     try {
    // Example using WMEmail class.
    // Get email object.
   // WMEmail wmemail = EmailTools.getWMEmailInstance("ismn@nla.gov.au", "ismn@nla.gov.au");
   WMEmail wmemail = EmailTools.getWMEmailInstance(_prop.getProperty("emailTo"), _prop.getProperty("emailFrom"));

    // Add more information.
    wmemail.setSubject("ISMN Agency Administration: Allocated ISMNs");
    wmemail.setTemplateName(_prop.getProperty("templateDir") + "/common/allocatedISMNmail.wm");
    wmemail.setData(argsData);
    //wmemail.addData("test","vinita");

    logit("TemplateName%%%%%%%%%%"+wmemail.getTemplateName());

    // Send email.
    EmailTools.send(wmemail);
    }
    catch (MessagingException me) {
      message = me.getMessage();
      _reply.put("message", message);
      return "/common/ismnerror";
    }
    return "done";
  } //sendMail



}// AllocateISMN




