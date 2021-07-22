package binutils.io;

import calbince.ExptData;
import calbince.PhaseData;
import java.io.*;
import java.util.StringTokenizer;

/**
 * @author : Shivam
 * @version : 2012.03.29 (SU): *Added Escape Character "//" for input file so
 * that weight need not to be zero to skip a point.
 *
 *
 */
public class DataReader {

    private static String cname = "DataReader";
    private String phaseInputFileName;
    private String dataInputFileName;
    private PhaseData phasedata;
    private ExptData exptdata;
    private int numPhases;

    public DataReader(String dataInputFileName, ExptData exptdata) throws IOException {//vj-2013-05-20
        Print.f(cname + ".constructor called", 6);
        this.dataInputFileName = dataInputFileName;
        this.exptdata = exptdata;
        //readExptDataFile(dataInputFileName, exptdata);//2017-01-26-VJ
        Print.f(cname + ".constructor ended", 6);
    }

    public DataReader(String phaseInputFileName, PhaseData phasedata) throws IOException {//vj-2013-05-20
        Print.f(cname + ".constructor called", 6);
        this.phaseInputFileName = phaseInputFileName;
        this.phasedata = phasedata;
        //readPhaseDataFile(phaseInputFileName, phasedata);//2017-01-26-VJ
        Print.f(cname + ".constructor ended", 6);
    }

    public void readExptDataFile() throws IOException {//VJ-2013-03-22
        Print.f(cname + ".readExptDataFile() Called with Input File : " + dataInputFileName, 7);
        int nDataPoints = DataReader.getNData(dataInputFileName);
        //Prnt.f("No. of data points (ndata)= " + nDataPoints, 4);
        // Local arrays to store Data from file
        double[][] arrayIn = new double[8][nDataPoints];
        String dataType[] = new String[nDataPoints];
        String reference[] = new String[nDataPoints];

        FileInputStream fin = null;
        try {
            fin = new FileInputStream(dataInputFileName);
        } catch (FileNotFoundException e) {
            System.err.println("File Not Found");
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Usage: ShowFile File");
        }
        DataInputStream in = new DataInputStream(fin);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String str;
        Boolean isStartReading = false, isBlank = true;
        int index = 0;
        while ((str = br.readLine()) != null) {
            if (str.trim().equalsIgnoreCase("#************************STOP#")) {
                break;
            } else if (str.trim().equalsIgnoreCase("#************************START#")) {
                isStartReading = true;
                str = br.readLine();
            }
            if (str.startsWith("//")) {
                continue;
            }
            if (isStartReading) {
                if (str.trim().equalsIgnoreCase("")) {
                    if (isBlank) {
                        Print.f("Input File for expData has blank Spaces and Tabs !", 0);
                        isBlank = false;
                    }
                } else {
                    StringTokenizer st = new StringTokenizer(str);
                    arrayIn[0][index] = Double.parseDouble(st.nextToken());//Index
                    dataType[index] = st.nextToken();//data Type
                    arrayIn[1][index] = Double.parseDouble(st.nextToken());//y
                    arrayIn[2][index] = Double.parseDouble(st.nextToken());//ymod
                    arrayIn[3][index] = Double.parseDouble(st.nextToken());//sigy
                    arrayIn[4][index] = Double.parseDouble(st.nextToken());//wt
                    arrayIn[5][index] = Double.parseDouble(st.nextToken());//x
                    arrayIn[6][index] = Double.parseDouble(st.nextToken());//x1
                    arrayIn[7][index] = Double.parseDouble(st.nextToken());//x2
                    reference[index] = st.nextToken();//reference
                    index++;
                }// else Closed
            }// if isStartReading condition closed
        }// while loop for whole file
        if (fin != null) {
            fin.close();
        }

        exptdata.init(dataInputFileName, nDataPoints);//vj-2012-04-18:Initialize exptdatas array with size nUsedDataPoints
        for (int idata = 0; idata < nDataPoints; idata++) {
            exptdata.setdataIndex(arrayIn[0][idata], idata);
            exptdata.setDataType(dataType[idata], idata);
            exptdata.setY(arrayIn[1][idata], idata);
            exptdata.setYmod(arrayIn[2][idata], idata);
            exptdata.setSig(arrayIn[3][idata], idata);
            exptdata.setWt(arrayIn[4][idata], idata);
            exptdata.setX(arrayIn[5][idata], idata);
            exptdata.setX1(arrayIn[6][idata], idata);
            exptdata.setX2(arrayIn[7][idata], idata);
            exptdata.setRefList(reference[idata], idata);
        }
        //exptdatas.printGlobalData();
        patternList(dataType, nDataPoints, exptdata);// It sets bList & pid on case basis.
        Print.f(cname + ".readExptDataFile() ended", 7);
    }//  Method  Closed

    private void patternList(String[] dataType, int nDataPoints, ExptData exptdata) throws IOException {//VJ-2013-03-22-Added
        Print.f(cname + ".patternList() method called", 7);
        String delims = "-", temp[];
        for (int i = 0; i < nDataPoints; i++) {
            temp = dataType[i].split(delims);
            //Print.f(temp+ "temp", 1);
            //Print.f(temp.length+ "temp.length", 1);
            exptdata.setBList(temp[0], i);

            switch (temp.length) {
                case 4:
                    exptdata.setPid(temp[1], i, 0, 0);
                    exptdata.setPid(temp[2], i, 0, 1);
                    exptdata.setPid(temp[3], i, 0, 2);
                    break;
                case 7:
                    exptdata.setPid(temp[1], i, 0, 0);
                    exptdata.setPid(temp[2], i, 0, 1);
                    exptdata.setPid(temp[3], i, 0, 2);
                    exptdata.setPid(temp[4], i, 1, 0);
                    exptdata.setPid(temp[5], i, 1, 1);
                    exptdata.setPid(temp[6], i, 1, 2);
                    break;
                case 10:
                    exptdata.setPid(temp[1], i, 0, 0);
                    exptdata.setPid(temp[2], i, 0, 1);
                    exptdata.setPid(temp[3], i, 0, 2);
                    exptdata.setPid(temp[4], i, 1, 0);
                    exptdata.setPid(temp[5], i, 1, 1);
                    exptdata.setPid(temp[6], i, 1, 2);
                    exptdata.setPid(temp[7], i, 2, 0);
                    exptdata.setPid(temp[8], i, 2, 1);
                    exptdata.setPid(temp[9], i, 2, 2);
                    break;
                default:
                    Print.f(cname + ".patternList: Phase description in expdata input is incorrect", 0);
                    System.exit(-1);
            }
        }// Closed for i loop
        Print.f(cname + ".patternList() Closed", 7);
    }// Closed  method

    public void readPhaseDataFile() throws FileNotFoundException, IOException {
        Print.f(cname + ".readPhaseDataFile Called with Input File : " + phaseInputFileName, 7);
        numPhases = DataReader.getNData(phaseInputFileName);
        Print.f("No. of data points (ndata)= " + numPhases, 7);
        phasedata.init(phaseInputFileName, numPhases);//vj-2012-04-19

        FileInputStream fin = null;
        try {
            fin = new FileInputStream(phaseInputFileName);
        } catch (FileNotFoundException e) {
            System.out.println("File Not Found");
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Usage: ShowFile File");
        }
        DataInputStream in = new DataInputStream(fin);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String str, delims;
        Boolean isStartReading = false, isBlank = true;
        int i = 0;
        while ((str = br.readLine()) != null) {
            //System.out.println("str" + str.trim());
            if (str.trim().equalsIgnoreCase("#************************STOP#")) {
                break;
            } else if (str.trim().equalsIgnoreCase("#************************START#")) {
                isStartReading = true;
                str = br.readLine();
            }
            if (str.startsWith("//")) {
                continue;
            }
            if (isStartReading) {
                if (str.trim().equalsIgnoreCase("")) {
                    if (isBlank) {
                        Print.f("Input File for PhaseData has blank Spaces and Tabs !", 0);
                        isBlank = false;
                    }
                } else {
                    StringTokenizer st = new StringTokenizer(str);
                    //phasedata.setPhaseIndex(Double.parseDouble(st.nextToken()), i);
                    phasedata.setPhaseIndex(i, i);

                    //Reading pids
                    delims = "-";
                    String[] temp = st.nextToken().split(delims);
                    //Print.f("temp",temp,0);
                    phasedata.setPidList(temp, i);

                    //Reading Linked pids
                    delims = "-";
                    String tempStr = st.nextToken();
                    String[] tempStr2 = {"-", "-", "-"};
                    if ("-".equals(tempStr)) {
                        phasedata.setLinkedPidList(tempStr2, i);
                    } else {
                        temp = tempStr.split(delims);
                        Print.f("temp",temp,0);
                        phasedata.setLinkedPidList(temp, i);
                    }
                    //Reading rest of the parameters 
                    int matchedPID = getPid(phasedata.getLinkedPid0(i), phasedata.getLinkedPid1(i), phasedata.getLinkedPid2(i));
                    //System.out.println("matchedPID:" + matchedPID);
                   
                    if (matchedPID == -1) {//Linked PID does not exist, read rest of the paramters from file
                        //System.out.println(st);
                        if (st.hasMoreTokens()) {
                            readPhaseDataParamters(st, i);
                        } else {
                            Print.f("Check Phase data file", 0);
                        }
                    } else {//Linked PID exists, read rest of the paramters from the corresponding PID
                        //Reading rest of the parameters  
                        phasedata.setComponentA(phasedata.getComponentA(matchedPID), i);
                        phasedata.setComponentB(phasedata.getComponentB(matchedPID), i);
                        phasedata.setStdst(phasedata.getStdst(matchedPID)[3], i);
                        phasedata.setNp(phasedata.getNp(matchedPID), i);
                        phasedata.setMList(phasedata.getMList(matchedPID), i);
                        phasedata.setIaList(phasedata.getIaList(matchedPID), i);
                        phasedata.setEList(phasedata.getEList(matchedPID), i);
                        phasedata.setEMatFileName(phasedata.getEMatFileName(matchedPID), i);
                        //Reading EMatFile
                        double[][] emat_local = new double[phasedata.getNp(i)][phasedata.getNp(i)];
                        readEmat(phasedata.getEMatFileName(i), emat_local);//get emat file name and read corresponding emat file into emat_local
                        phasedata.setEMatList(emat_local, i);// set emat file
                        //readPhaseDataParamters(st, i);
                    }
                    i++;
                }// else Closed
            }// if isStartReading condition closed
        }// while loop for whole file
        if (fin != null) {
            fin.close();
        }
        Print.f(cname + ".readPhaseDataFile Closed", 7);
    }

    private void readPhaseDataParamters(StringTokenizer st, int i) throws IOException {
        String delims;
        //Reading Elements
        phasedata.setComponentA(st.nextToken(), i);
        phasedata.setComponentB(st.nextToken(), i);

        //Reading Standard State 
        phasedata.setStdst(st.nextToken(), i);

        //Reading no of optimization paramters
        phasedata.setNp(Integer.parseInt(st.nextToken()), i);

        //Reading MList
        delims = ", *";
        String strMList = st.nextToken();
        String[] temp = strMList.split(delims);
        for (int k = 0; k <= (phasedata.getNp(i) / 2); k++) {
            phasedata.setMList(Double.parseDouble(temp[k]), i, k);
        }// k loop closed

        //Reading IaList
        String strIaList = st.nextToken();
        temp = strIaList.split(delims);
        for (int k = 0; k < phasedata.getNp(i); k++) {//vj-2014-08-27
            if (temp[k].equalsIgnoreCase("true") || temp[k].equalsIgnoreCase("false")) {
                phasedata.setIaList(Boolean.parseBoolean(temp[k]), i, k);
            } else {
                boolean isTrue = false;
                if (Double.parseDouble(temp[k]) == 1.0) {
                    isTrue = true;
                }
                phasedata.setIaList(isTrue, i, k);
            }
        }// k loop closed

        //Reading EList
        String strEList = st.nextToken();
        temp = strEList.split(delims);
        for (int k = 0; k < phasedata.getNp(i); k++) {//vj-2014-08-27
            phasedata.setEList(Double.parseDouble(temp[k]), i, k);
        }// k loop closed

        //Reading EMatFileName
        phasedata.setEMatFileName(st.nextToken(), i);//Set emat file name
        double[][] emat_local = new double[phasedata.getNp(i)][phasedata.getNp(i)];

        //Reading EMatFile 
        readEmat(phasedata.getEMatFileName(i), emat_local);//get emat file name and read corresponding emat file into emat_local
        phasedata.setEMatList(emat_local, i);// set emat file
    }

    public int getPid(String pType, String pModel, String pInstance) throws IOException {//vj-2013-05-19-Modified
        Print.f(cname + ".getpIndex() method called", 7);
        //System.out.println(pType+","+pModel+","+pInstance);
        for (int i = 0; i < numPhases; i++) {
            //System.out.println(i);
            //System.out.println(phasedata.getPid0(i)+","+phasedata.getPid1(i)+","+phasedata.getPid2(i));
            if (phasedata.getPid0(i) == null) {
                continue;
            }
            if ((phasedata.getPid0(i).equalsIgnoreCase(pType)) && (phasedata.getPid1(i).equalsIgnoreCase(pModel)) && (phasedata.getPid2(i).equalsIgnoreCase(pInstance))) {
                Print.f(cname + ".getpIndex() method ended with pIndex:" + i, 7);
                return (phasedata.getPhaseIndex(i));
            }
        }//2012=02-16(VJ): Modified
        Print.f(cname + ".getpIndex() method with pIndex:" + "-1", 7);
        return (-1);
    }

    public static int getNData(String infile) throws FileNotFoundException, IOException {
        int ndat;
        //Print.f(cname + ".getNData called with Input File:" + infile, 7);
        LineNumberReader lineCounter = new LineNumberReader(new InputStreamReader(new FileInputStream(infile)));
        String nextLine = null;
        int commentLines = 0, itr = 0;
        try {
            while ((nextLine = lineCounter.readLine()) != null) {

                if (nextLine.trim().equalsIgnoreCase("#************************STOP#")) {
                    commentLines++;
                    break;
                } else if (nextLine.trim().equalsIgnoreCase("#************************START#")) {
                    itr++;
                    commentLines++;
                } else if ((itr == 0) || (itr == 1 && (nextLine.trim().equalsIgnoreCase("") || nextLine.startsWith("//")))) {
                    commentLines++;
                }
            }
        } catch (Exception done) {
            System.err.println("Exception in Reading ndata in getNData()");
        }
        ndat = lineCounter.getLineNumber() - commentLines;
        //Print.f(cname + ".getNData ended with ndat:" + ndat, 7);
        return (ndat);
    }// Close getNData() Method

    public void readEmat(String infile, double[][] eMat) throws FileNotFoundException, IOException {
        Print.f(cname + ".setEmat called with" + infile, 7);
        String currentDirectory = System.getProperty("user.dir");
        String phaseIn = currentDirectory + "/../data/" + infile;
        //System.out.println(phaseIn);
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(phaseIn);
        } catch (FileNotFoundException e) {
            System.out.println("File Not Found");
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Usage: ShowFile File");
        }
        DataInputStream in = new DataInputStream(fin);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String str;
        int dim = DataReader.getNData(phaseIn);
        int itr = 0, i = 0;
        double eTransMat_local[][] = new double[dim][dim];

        while ((str = br.readLine()) != null) {
            if (str.equalsIgnoreCase("#************************STOP#")) {
                break;
            }
            if (str.equalsIgnoreCase("#************************START#")) {
                itr++;
                str = br.readLine();
            }
            if (itr == 1) {
                if (str.trim().equalsIgnoreCase("")) {
                    //System.out.println("Input File for seteTrans has blank Spaces and Tabs !");
                } else {
                    String delims = ", *";
                    while (i < eTransMat_local.length) {
                        StringTokenizer st = new StringTokenizer(str);
                        for (int j = 0; j < eTransMat_local[i].length; j++) {
                            eTransMat_local[i][j] = Double.parseDouble(st.nextToken());
                        }
                        i++;
                        break;
                    }// Closed while i loop
                }// else Closed
            }// if itr==1 condition closed
        }// while loop on whole file Closed
        fin.close();
        //Print.f("eTransMat_local",eTransMat_local, 0);
        for (int ii = 0; ii < dim; ii++) {
            System.arraycopy(eTransMat_local[ii], 0, eMat[ii], 0, dim);
        }
        //eMat = eTransMat_local;
        Print.f(cname + ".setEmat ended", 7);
    }// Closed Method setEcMat()
}// Closed class FilIO
