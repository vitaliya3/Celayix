import com.opencsv.CSVWriter;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import javax.xml.soap.Node;
import org.xml.sax.SAXException;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.*;
import javax.xml.xpath.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class CreateSOAPMessage {
    public static void main(String[] args) throws Exception {
        String file = "testfile.csv";
        System.setProperty("javax.xml.soap.SAAJMetaFactory", "com.sun.xml.messaging.saaj.soap.SAAJMetaFactoryImpl");

        String FromDate = "12/25/2018";
        String ToDate = "12/25/2018";


        //calls loginAPI call and returns new session id
        String sessionID = makeLoginAPICall();

        //saves shifts from the given file
        makeSaveShiftsCall(file,sessionID);

        //reads shifts for the given date and creates a CSV file with output
        makeCSVFile(sessionID, FromDate, ToDate);



    }

    //create body and header
    public static SOAPMessage createSoapMessage() {

        try {
            MessageFactory factory = MessageFactory.newInstance();
            SOAPMessage soapMsg = factory.createMessage();
            SOAPPart part = soapMsg.getSOAPPart();
            soapMsg.getSOAPHeader().detachNode();

            SOAPEnvelope envelope = part.getEnvelope();
            SOAPElement eleXSINs = envelope.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");

            SOAPBody body = envelope.getBody();

            body.addNamespaceDeclaration("s1", "http://www.w3.org/2001/XMLSchema");
            body.addNamespaceDeclaration("s0", "urn:AiraTech:eTimeWS");

            return soapMsg;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    //creates LoginRequest
    public static SOAPMessage createLoginRequest() {
        try {
            SOAPMessage soapmsg = createSoapMessage();
            SOAPElement login2 = soapmsg.getSOAPBody().addChildElement("s0:apSrv");
            SOAPElement dsContext = login2.addChildElement("dsContext");
            makeTtContext(dsContext, "pcPassword", "MnPt17z");
            makeTtContext(dsContext, "pcUserID", "wsdl");
            soapmsg.writeTo(System.out);
            return soapmsg;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static SOAPMessage createReadShifts(String sessionID, String FromDate, String ToDate) {

        try {
            SOAPMessage soapMsg = createSoapMessage();

            SOAPElement shifts2 = soapMsg.getSOAPBody().addChildElement("s0:apSrvShiftGet");

            SOAPElement dsContext = shifts2.addChildElement("dsContext");

            makeTtContext(dsContext, "pcSessionID", sessionID);
            makeTtContext(dsContext, "pcUserId", "wsdl");
            makeTtContext(dsContext, "pcWhere", "");
            makeTtContext(dsContext, "pdFromDate", FromDate);
            makeTtContext(dsContext, "pdToDate", ToDate);
            makeTtContext(dsContext, "piBranchID", "2");
            makeTtContext(dsContext, "piCompanyID", "2");
            //makeTtContext(dsContext, "pcAction","ADD_UPDATE");
            System.out.println();


            soapMsg.writeTo(System.out);
            return soapMsg;


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void makeSaveShiftsCall(String file, String sessionID){
        System.out.println();
        System.out.println("Making SaveShift call");
        System.out.println();
        SOAPMessage saveShiftRequest = createSaveShifts(file, sessionID);
        SOAPMessage saveShiftResponse = callSoapWebService(saveShiftRequest);

    }
    //creates SaveShifts request
    public static SOAPMessage createSaveShifts(String file, String pcSessionID) {

        List<Shift> shiftList = CSVReader.readShiftsFromCSV(file);
        try {

            SOAPMessage soapMsg = createSoapMessage();

            SOAPElement shifts2 = soapMsg.getSOAPBody().addChildElement("s0:apSrvShiftSave");

            SOAPElement dsContext = shifts2.addChildElement("dsContext");


            makeTtContext(dsContext, "pcAction", "ADD_UPDATE");
            makeTtContext(dsContext, "pcSessionID", pcSessionID);
            makeTtContext(dsContext, "pcUserID", "wsdl");
            makeTtContext(dsContext, "piBranchID", "2");
            makeTtContext(dsContext, "piCompanyID", "2");

            SOAPElement dsShifts = shifts2.addChildElement("dsShifts");

            for (Shift b : shiftList) {
                makettShifts(dsShifts, b.getStartDate(), b.getStartTime(), b.getEndTime(), b.getArea());
            }

            soapMsg.writeTo(System.out);
            //System.out.println();
            return soapMsg;


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    //creates TTContext element for Soap Message
    public static SOAPElement makeTtContext(SOAPElement element, String contextName, String contextValue) {

        try {

            QName childname = new QName("ttContext");
            SOAPElement ttContext = element.addChildElement("ttContext");
            childname = new QName("contextGroup");
            SOAPElement contextGroup = ttContext.addChildElement(childname);
            contextGroup.addTextNode("PARAM");

            childname = new QName("contextName");
            SOAPElement contextName1 = ttContext.addChildElement(childname);
            contextName1.addTextNode(contextName);

            childname = new QName("contextValue");
            SOAPElement contextValue1 = ttContext.addChildElement(childname);
            contextValue1.addTextNode(contextValue);

            childname = new QName("contextOperator");
            ttContext.addChildElement(childname);


            childname = new QName("contextType");
            ttContext.addChildElement(childname);

            return ttContext;

        } catch (SOAPException e1) {
            e1.printStackTrace();
        }


        return null;

    }




    //creates Shifts for soap requests
    public static SOAPElement makettShifts(SOAPElement element, String shiftDate, String timeStart, String timeEnd, String shiftType) {

        try {
            QName childname = new QName("ttShifts");
            SOAPElement ttShifts = element.addChildElement("ttShifts");


            SOAPElement shiftid = ttShifts.addChildElement("shiftid");
            shiftid.addTextNode("0");

            SOAPElement cid = ttShifts.addChildElement("cid");
            cid.addTextNode("2");

            SOAPElement bid = ttShifts.addChildElement("bid");
            bid.addTextNode("2");

            SOAPElement uid = ttShifts.addChildElement("uid");
            uid.addTextNode("1");

            SOAPElement sid = ttShifts.addChildElement("sid");
            sid.addTextNode("1");

            SOAPElement eid = ttShifts.addChildElement("eid");
            eid.addTextNode("0");

            SOAPElement shdate = ttShifts.addChildElement("shdate");
            shdate.addTextNode(shiftDate);

            SOAPElement tmstart = ttShifts.addChildElement("tmstart");
            tmstart.addTextNode(timeStart);

            SOAPElement tmend = ttShifts.addChildElement("tmend");
            tmend.addTextNode(timeEnd);

            SOAPElement breaks = ttShifts.addChildElement("breaks");
            breaks.addTextNode("0");

            SOAPElement emsrtycd = ttShifts.addChildElement("emsrtycd");
            emsrtycd.addTextNode(shiftType);

            SOAPElement chkin = ttShifts.addChildElement("chkin");
            chkin.addTextNode("false");

            SOAPElement chkout = ttShifts.addChildElement("chkout");
            chkout.addTextNode("false");

            SOAPElement actmstrt = ttShifts.addChildElement("actmstrt");
            actmstrt.addTextNode("0");

            SOAPElement actmend = ttShifts.addChildElement("actmend");
            actmend.addTextNode("0");

            SOAPElement actbreak = ttShifts.addChildElement("actbreak");
            actbreak.addTextNode("0");


            SOAPElement safetime = ttShifts.addChildElement("safetime");
            safetime.addTextNode("");

            SOAPElement bilno = ttShifts.addChildElement("bilno");
            bilno.addTextNode("0");


            SOAPElement shotexmp = ttShifts.addChildElement("shotexmp");
            shotexmp.addTextNode("false");

            SOAPElement chotemdt = ttShifts.addChildElement("chotemdt");
            chotemdt.addNamespaceDeclaration("nil", "true");

            SOAPElement billot = ttShifts.addChildElement("billot");
            billot.addTextNode("false");

            SOAPElement shtent = ttShifts.addChildElement("shtent");
            shtent.addTextNode("false");

            SOAPElement snote = ttShifts.addChildElement("snote");
            snote.addTextNode("");

            SOAPElement recalcon = ttShifts.addChildElement("recalcon");
            recalcon.addTextNode("true");


            SOAPElement unsched = ttShifts.addChildElement("unsched");
            unsched.addTextNode("false");

            SOAPElement pylck = ttShifts.addChildElement("pylck");
            pylck.addTextNode("false");

            SOAPElement payldate = ttShifts.addChildElement("payldate");
            payldate.addNamespaceDeclaration("nil", "true");


            SOAPElement spaid = ttShifts.addChildElement("spaid");
            spaid.addTextNode("false");

            SOAPElement spaidate = ttShifts.addChildElement("spaidate");
            spaidate.addNamespaceDeclaration("nil", "true");

            SOAPElement pclosed = ttShifts.addChildElement("pclosed");
            pclosed.addTextNode("false");


            SOAPElement pclodate = ttShifts.addChildElement("pclodate");
            pclodate.addNamespaceDeclaration("nil", "true");


            SOAPElement billck = ttShifts.addChildElement("billck");
            billck.addTextNode("false");

            SOAPElement bilodate = ttShifts.addChildElement("bilodate");
            bilodate.addNamespaceDeclaration("nil", "true");

            SOAPElement sbilled = ttShifts.addChildElement("sbilled");
            sbilled.addTextNode("false");

            SOAPElement sbildate = ttShifts.addChildElement("sbildate");
            sbildate.addNamespaceDeclaration("nil", "true");

            SOAPElement bclosed = ttShifts.addChildElement("bclosed");
            bclosed.addTextNode("false");

            SOAPElement bclodate = ttShifts.addChildElement("bclodate");
            bclodate.addNamespaceDeclaration("nil", "true");


            SOAPElement shtype = ttShifts.addChildElement("shtype");
            shtype.addTextNode("");

            SOAPElement safetyno = ttShifts.addChildElement("safetyno");
            safetyno.addTextNode("0");

            SOAPElement crbytmen = ttShifts.addChildElement("crbytmen");
            crbytmen.addTextNode("false");

            SOAPElement grpshid = ttShifts.addChildElement("grpshid");
            grpshid.addTextNode("0");

            SOAPElement autofill = ttShifts.addChildElement("autofill");
            autofill.addTextNode("false");

            SOAPElement cruser = ttShifts.addChildElement("cruser");
            cruser.addTextNode("");

            SOAPElement crdate = ttShifts.addChildElement("crdate");
            crdate.addTextNode("2018-12-12");

            SOAPElement crtime = ttShifts.addChildElement("crtime");
            crtime.addTextNode("0");

            SOAPElement shstatcd = ttShifts.addChildElement("shstatcd");
            shstatcd.addTextNode("");

            SOAPElement workdesc = ttShifts.addChildElement("workdesc");
            workdesc.addTextNode("");

            SOAPElement shtagsetcd = ttShifts.addChildElement("shtagsetcd");
            shtagsetcd.addTextNode("");

            SOAPElement ChangeGroupCode = ttShifts.addChildElement("ChangeGroupCode");
            ChangeGroupCode.addTextNode("");

            SOAPElement ChangeGroupType = ttShifts.addChildElement("ChangeGroupType");
            ChangeGroupType.addTextNode("0");

            SOAPElement schStartTime = ttShifts.addChildElement("schStartTime");
            schStartTime.addTextNode("2018-12-12T00:00:00.000");

            SOAPElement schEndTime = ttShifts.addChildElement("schEndTime");
            schEndTime.addTextNode("2018-12-12T00:00:00.000");

            SOAPElement wrkStartTime = ttShifts.addChildElement("wrkStartTime");
            wrkStartTime.addTextNode("2018-12-12T00:00:00.000");

            SOAPElement wrkEndTime = ttShifts.addChildElement("wrkEndTime");
            wrkEndTime.addTextNode("2018-12-12T00:00:00.000");

            SOAPElement SelfSchStart = ttShifts.addChildElement("SelfSchStart");
            SelfSchStart.addNamespaceDeclaration("nil", "true");

            SOAPElement SelfSchEnd = ttShifts.addChildElement("SelfSchEnd");
            SelfSchEnd.addNamespaceDeclaration("nil", "true");

            SOAPElement LocationObj = ttShifts.addChildElement("LocationObj");
            LocationObj.addTextNode("0");

            SOAPElement SelfSchAssignTime = ttShifts.addChildElement("SelfSchAssignTime");
            SelfSchAssignTime.addNamespaceDeclaration("nil", "true");

            SOAPElement PublishType = ttShifts.addChildElement("PublishType");
            PublishType.addTextNode("0");

            SOAPElement ref1 = ttShifts.addChildElement("ref1");
            ref1.addTextNode("");

            SOAPElement ref2 = ttShifts.addChildElement("ref2");
            ref2.addTextNode("");

            SOAPElement ref3 = ttShifts.addChildElement("ref3");
            ref3.addTextNode("");

            SOAPElement ref4 = ttShifts.addChildElement("ref4");
            ref4.addTextNode("");

            SOAPElement ref5 = ttShifts.addChildElement("ref5");
            ref5.addTextNode("");

            SOAPElement disp_crtime = ttShifts.addChildElement("disp_crtime");
            disp_crtime.addTextNode("");

            SOAPElement DbRow = ttShifts.addChildElement("DbRow");
            DbRow.addTextNode("0x000000");

            SOAPElement iImport = ttShifts.addChildElement("iImport");
            iImport.addTextNode("0");

            SOAPElement cObjectName = ttShifts.addChildElement("cObjectName");
            cObjectName.addTextNode("");

            SOAPElement cValmess = ttShifts.addChildElement("cValmess");
            cValmess.addTextNode("");

            SOAPElement cChkNew = ttShifts.addChildElement("cChkNew");
            cChkNew.addTextNode("A");

            SOAPElement MidDiffScheduled = ttShifts.addChildElement("MidDiffScheduled");
            MidDiffScheduled.addTextNode("0");

            SOAPElement MidDiffOverlap1 = ttShifts.addChildElement("MidDiffOverlap1");
            MidDiffOverlap1.addTextNode("0");

            SOAPElement MidDiffOverlap2 = ttShifts.addChildElement("MidDiffOverlap2");
            MidDiffOverlap2.addTextNode("0");


            SOAPElement MidDiffWorked = ttShifts.addChildElement("MidDiffWorked");
            MidDiffWorked.addTextNode("0");

            SOAPElement MidDiffWorkedOverlap1 = ttShifts.addChildElement("MidDiffWorkedOverlap1");
            MidDiffWorkedOverlap1.addTextNode("0");

            SOAPElement MidDiffWorkedOverlap2 = ttShifts.addChildElement("MidDiffWorkedOverlap2");
            MidDiffWorkedOverlap2.addTextNode("0");

            SOAPElement CustomerName = ttShifts.addChildElement("CustomerName");
            CustomerName.addTextNode("");

            SOAPElement CusShortName = ttShifts.addChildElement("CusShortName");
            CusShortName.addTextNode("");


            SOAPElement SiteName = ttShifts.addChildElement("SiteName");
            SiteName.addTextNode("");

            SOAPElement StShortName = ttShifts.addChildElement("StShortName");
            StShortName.addTextNode("");


            SOAPElement EmpFirstName = ttShifts.addChildElement("EmpFirstName");
            EmpFirstName.addTextNode("");


            SOAPElement EmpLastName = ttShifts.addChildElement("EmpLastName");
            EmpLastName.addTextNode("");

            SOAPElement EmpShortName = ttShifts.addChildElement("EmpShortName");
            EmpShortName.addTextNode("");

            SOAPElement LocationCode = ttShifts.addChildElement("LocationCode");
            LocationCode.addTextNode("");

            SOAPElement LocationDscr = ttShifts.addChildElement("LocationDscr");
            LocationDscr.addTextNode("");

            SOAPElement ServiceDescr = ttShifts.addChildElement("ServiceDescr");
            ServiceDescr.addTextNode("");

            SOAPElement ShiftTotal = ttShifts.addChildElement("ShiftTotal");
            ShiftTotal.addTextNode("0.0");

            SOAPElement Acttotal = ttShifts.addChildElement("Acttotal");
            Acttotal.addTextNode("0.0");

            SOAPElement chcrtime = ttShifts.addChildElement("chcrtime");
            chcrtime.addTextNode("");

            return ttShifts;

        } catch (SOAPException e1) {
            e1.printStackTrace();
        }

        return null;
    }

//sends Soap Message to the server and returns response

    public static SOAPMessage callSoapWebService(SOAPMessage request) {
        try {
            String soapEndpointUrl = "http://cws-airatech.celayix.com/wsa/wsa1";
            // Create SOAP Connection
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

            // Send SOAP Message to SOAP Server
            SOAPMessage soapResponse = soapConnection.call(request, soapEndpointUrl);

            // Print the SOAP Response

            System.out.println();

            System.out.println();

            System.out.println("Response SOAP Message:");
            System.out.println();


            soapResponse.writeTo(System.out);
            System.out.println();


            soapConnection.close();
            return soapResponse;

        } catch (Exception e) {
            System.err.println("\nError occurred while sending SOAP Request to Server!");
            e.printStackTrace();
        }
        return null;

    }


    //login to server to get new sessionID
    public static String makeLoginAPICall() throws ParserConfigurationException, IOException, SOAPException, SAXException, XPathExpressionException {
        System.out.println();
        System.out.println("Making Login API Call");
        SOAPMessage loginRequest = createLoginRequest();
        SOAPMessage loginResponse = callSoapWebService(loginRequest);
        String sessionID = "";

        SOAPBody body = loginResponse.getSOAPBody();

        NodeList nl = body.getElementsByTagName("apSrvResponse");


        if (nl != null) {
            int length = nl.getLength();
            for (int i = 0; i < length; i++) {
                if (nl.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    Element el = (Element) nl.item(i);
                    if (el.getNodeName().contains("apSrvResponse")) {
                        String contextValue = el.getElementsByTagName("contextValue").item((1)).getTextContent();
                        sessionID = contextValue;
                        //System.out.println();
                        // System.out.println(("sessionid from method "+contextValue));

                    }

                }

            }


        }

        return sessionID;

    }


//makes readShifts call and creates a CSV file
    public static void makeCSVFile(String sessionID, String FromDate, String ToDate) throws ParserConfigurationException, IOException, SOAPException, SAXException, XPathExpressionException {
        //File file = new File("/Users/Vita/SOAPMessages/test.csv");

        FileWriter outputfile = new FileWriter("/Users/Vita/SOAPMessages/test.csv");
        List<String[]> data = new ArrayList<String[]>();

        // create CSVWriter object filewriter object as parameter
        //CSVWriter writer = new CSVWriter(outputfile);
        CSVWriter writer = new CSVWriter(outputfile, ',',
                CSVWriter.NO_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END);


        System.out.println("Making API call to read shifts for: "+FromDate+"-"+ToDate);
        SOAPMessage readshifts = createReadShifts(sessionID, FromDate, ToDate);
        SOAPMessage readShiftResponse = callSoapWebService(readshifts);


        SOAPBody body = readShiftResponse.getSOAPBody();

        NodeList nl = body.getElementsByTagName("ttShifts");

        if (nl != null) {

            int length = nl.getLength();
            for (int i = 0; i < length; i++) {
                if (nl.item(i).getNodeType() == Node.ELEMENT_NODE) {

                    Element el = (Element) nl.item(i);

                     //data.add(nw String[]{shiftId, cid, bid});

                    NodeList shiftsInfo = el.getChildNodes();

                    String[] row1 = new String[shiftsInfo.getLength()];
                    String[] row = new String[shiftsInfo.getLength()];

                    for (int k = 0; k < row1.length;) {

                        for (int j = 0; j < shiftsInfo.getLength(); j++) {

                            Node childNode = (Node) shiftsInfo.item(j);
                            row1[k] = childNode.getNodeName();
                            row[k] = childNode.getTextContent();
                            k++;
                        }

                    }

                     if(i==0) {
                        data.add(row1);
                     }

                     data.add(row);

                }


            }
            writer.writeAll(data);
            writer.close();


        }

    }


}





















