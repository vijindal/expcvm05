/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package calbince;

import binutils.io.Print;
import binutils.stat.Utils;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import phase.*;
import phase.solution.calphad.RK;
import phase.solution.calphad.STCOMP;
import phase.solution.cecvm.bcc.A2ORCBINCE;
import phase.solution.cecvm.bcc.A2TBINCE;
import phase.solution.cecvm.bcc.B2TBINCE;
import phase.solution.cecvm.cph.A3TOBINCE;
import phase.solution.cecvm.cph.B19TOBINCE;
import phase.solution.cecvm.cph.D019TOBINCE;
import phase.solution.cecvm.fcc.A1QTBINCE;
import phase.solution.cecvm.fcc.A1TOBINCE;
import phase.solution.cecvm.fcc.L10TOBINCE;
import phase.solution.cecvm.fcc.L12TOBINCE;

/**
 *
 * @author admin
 */
public class PhaseData {
    //Phase specific Parameters

    private String phaseFileName = null;//vj-2012-03-25
    private int nPhases = 0;//vj-2012-04-19
    private int phaseIndex[] = null;//vj-2012-04-19
    private String[][] pidList = null;//For storing phase ids e.g. A1-TO-1, L10-TO-1
    private String[][] linkedPidList = null;//For storing linked phase ids (disordered phase ids) e.g. A1-TO-1, A2-T-1
    private String[] compA = null;//Symbols for component A e.g. Cd
    private String[] compB = null;//Symbols for component B e.g. Mg
    private String stdst[][] = null;
    private int[] npList = null;
    private double mList[][] = null;
    private boolean[][] iaList = null;
    private double eList[][] = null;
    private double[][][] eMatList = null;
    private String[] eMatFileName = null;//vj-2015-03-11-List of Ematfile names
//Fitmrq Parameters
    private int npt = 0;//vj-2012-04-20: Total no of optimization paramters
    private int[] aindex = null;
    private double a[] = null;
    private boolean ia[] = null;
    private double[] dyda = null;//vj-2013-04-01
    private String[][] strOpt = null;//vj-2013-04-04, for storing fitmrq calculations
    private int maxFitItr;//vj-2013-04-04-No of fitmrq iterations
    private String[][] outStrFirmrq = null;//vj-2012-04-21, for storing fitmrq calculations 
    private final String cname = "PhaseData";

    public PhaseData() throws IOException {
        Print.f(cname + ".constructor called", 6);
        Print.f(cname + ".constructor ended", 6);
    }

    public void init(String phaseFileNameIn, int nPhaseUsedIn) throws IOException {//vj-2012-04-19
        phaseFileName = phaseFileNameIn;
        nPhases = nPhaseUsedIn;
        phaseIndex = new int[nPhases];
        pidList = new String[nPhases][3];//Phase-Model-Instance e.g. A2-T-1
        linkedPidList = new String[nPhases][3];//Phase-Model-Instance e.g. A2-T-1
        compA = new String[nPhases];
        compB = new String[nPhases];
        stdst = new String[nPhases][4];
        npList = new int[nPhases];
        mList = new double[nPhases][];
        iaList = new boolean[nPhases][];
        eList = new double[nPhases][];
        eMatFileName = new String[nPhases];//vj-15-03-11
        eMatList = new double[nPhases][][];
    }
    //Setter Methods

    public void setPhaseIndex(int xIn, int i) {
        phaseIndex[i] = xIn;
    }

    public void setPidList(String[] xIn, int i) {//vj-2013-05-19
        pidList[i][0] = xIn[0];
        pidList[i][1] = xIn[1];
        pidList[i][2] = xIn[2];
    }

    public void setLinkedPidList(String[] xIn, int i) {//vj-2017-02-02
        linkedPidList[i][0] = xIn[0];
        linkedPidList[i][1] = xIn[1];
        linkedPidList[i][2] = xIn[2];
    }

    public void setComponentA(String xIn, int i) {
        compA[i] = xIn;
    }

    public void setComponentB(String xIn, int i) {
        compB[i] = xIn;
    }

    public void setStdst(String xIn, int i) {
        stdst[i][0] = pidList[i][0];
        stdst[i][1] = compA[i];
        stdst[i][2] = compB[i];
        stdst[i][3] = xIn;
    }

    public void setNp(int xIn, int i) throws IOException {
        npList[i] = xIn;
        mList[i] = new double[((int) npList[i] / 2) + 1];// mList length is 1 more than eList
        iaList[i] = new boolean[npList[i]];//vj-2014-08-27
        eList[i] = new double[npList[i]];//vj-2014-08-27
    }

    public void setMList(double xIn, int i, int j) throws IOException {
        mList[i][j] = xIn;
    }

    public void setMList(double[] xIn, int i) throws IOException {
        mList[i] = xIn;
    }

    public void setIaList(Boolean xIn, int i, int j) {
        iaList[i][j] = xIn;
    }

    public void setIaList(boolean[] xIn, int i) {
        iaList[i] = xIn;
    }

    public void setEList(double xIn, int i, int j) {
        eList[i][j] = xIn;
    }

    public void setEList(double[] xIn, int i) {
        System.arraycopy(xIn, 0, eList[i], 0, xIn.length);
    }

    public void setEMatFileName(String xIn, int i) {
        eMatFileName[i] = xIn;
    }

    public void setEMatList(double[][] xIn, int i) {
        eMatList[i] = xIn;
    }

    private boolean isLinkedPID(int i) {
        String[] tempStr = {"-", "-", "-"};
        return (Arrays.equals(linkedPidList[i], pidList[i])) || (Arrays.equals(linkedPidList[i], tempStr));
    }

    public void setFitParam() throws IOException {
        int sum = 0;
        String[] tempStr = {"-", "-", "-"};
        for (int i = 0; i < getNumPhases(); i++) {
            if ((Arrays.equals(linkedPidList[i], pidList[i])) || (Arrays.equals(linkedPidList[i], tempStr))) {
                sum = sum + getNp(i);
            }
        }
        //System.out.println("sum:" + sum);
        npt = sum;
        ia = new boolean[sum];
        a = new double[sum];
        dyda = new double[sum];
        aindex = new int[getNumPhases()];
        int itr = 0;
        for (int i = 0; i < getNumPhases(); i++) {
            //Print.f("pidList[i]", linkedPidList[i],1);
            if ((Arrays.equals(linkedPidList[i], pidList[i])) || (Arrays.equals(linkedPidList[i], tempStr))) {
                for (int j = 0; j < getNp(i); j++) {
                    ia[itr] = getIaList(i)[j];
                    a[itr] = getEList(i)[j];
                    itr++;
                }
            }
        }
// for i loop closed
        itr = 0;
        for (int i = 0; i < getNumPhases(); i++) {
            if ((Arrays.equals(linkedPidList[i], pidList[i])) || (Arrays.equals(linkedPidList[i], tempStr))) {
                aindex[i] = itr;
                itr += getIaList(i).length;
            }
        }// for i loop closed
    }

    public void setA(double xIn, int i) {
        a[i] = xIn;
    }

    //This method assigns array "aIn" to "a", splits array "a" into "aSplit", assigns "aSplit" to "Elist"   
    public void setA(double[] aIn) throws IOException {//vj-2013-04-09
        Print.f(cname + ".setA method called", 7);
        System.arraycopy(aIn, 0, a, 0, aIn.length);
        double[][] aSplit;
        aSplit = new double[nPhases][];
        for (int ip = 0; ip < nPhases; ip++) {
            aSplit[ip] = new double[npList[ip]];
        }
        splitA(a, aSplit);
        //Print.f("aSplit", aSplit, 0);
        for (int ip = 0; ip < nPhases; ip++) {
            setEList(aSplit[ip], ip);
        }
        //Print.f("a", a, 0);
        Print.f(cname + ".setA method ended", 7);
    }

    //This method split parmater list "a" into nPhases lists, to be used as parameter lists for individual phases
    private void splitA(double[] aIn, double[][] aOut) throws IOException {//vj-2015-11-19
        Print.f(cname + ".splitA method called", 7);
        for (int ip = 0; ip < nPhases; ip++) {
            System.arraycopy(a, aindex[ip], aOut[ip], 0, aOut[ip].length);
        }
        Print.f(cname + ".splitA method ended", 7);
    }

    public void initStrOpt(int imax) {
        strOpt = new String[imax + 1][6];
    }

    public void setMaxItrFit(int imax) {//vj-2013-04-04
        this.maxFitItr = imax;
    }

    public void setDyda(double[] dydaIn) {//vj-2013-04-01
        System.arraycopy(dydaIn, 0, this.dyda, 0, dydaIn.length);
    }
    //Getter Methods

    public String getPhaseFileName() {
        return (phaseFileName);
    }

    public int getNumPhases() {
        return (nPhases);
    }

    public int getPhaseIndex(int i) {
        return (phaseIndex[i]);
    }

    public String getPid0(int i) {//Returns only phase name 
        return (pidList[i][0]);
    }

    public String getPid1(int i) {//vj-2013-05-19-Returns only model of the phase
        return (pidList[i][1]);
    }

    public String getPid2(int i) {//Returns only instance of the phase
        return (pidList[i][2]);
    }

    public String getLinkedPid0(int i) {//Returns only phase name 
        return (linkedPidList[i][0]);
    }

    public String getLinkedPid1(int i) {//vj-2013-05-19-Returns only model of the phase
        return (linkedPidList[i][1]);
    }

    public String getLinkedPid2(int i) {//Returns only instance of the phase
        return (linkedPidList[i][2]);
    }

    public String getComponentA(int i) {
        return (compA[i]);
    }

    public String getComponentB(int i) {
        return (compB[i]);
    }

    public String[] getStdst(int i) {
        return (stdst[i]);
    }

    public int getNp(int i) {
        return (npList[i]);
    }

    public double[] getMList(int i) {
        return (mList[i]);
    }

    public String getStrM(int i) throws IOException {
        String strM = "";
        for (int inp = 0; inp <= (npList[i] / 2); inp++) {
            strM = strM + mList[i][inp] + " ";
        }
        return (strM);
    }

    public boolean[] getIaList(int i) {
        return (iaList[i]);
    }

    public String getStrIa(int i) {
        String strIa = "";
        for (int inp = 0; inp < npList[i]; inp++) {
            strIa = strIa + (iaList[i][inp] ? 1 : 0) + " ";
        }
        return (strIa);
    }

    public String getStr(boolean[] arr) {
        String strIa = "{";
        for (int inp = 0; inp < arr.length; inp++) {
            strIa = strIa + (arr[inp] ? 1 : 0) + ",";
        }
        strIa = strIa + "}";
        return (strIa);
    }

    public double[] getEList(int i) {
        return (eList[i]);
    }

    public String getEMatFileName(int i) {
        return (eMatFileName[i]);
    }

    public double[][] getEMatList(int i) {
        return (eMatList[i]);
    }

    public String getStrE(int i) {
        String strE = "";
        for (int inp = 0; inp < npList[i]; inp++) {
            if (!iaList[i][inp] && Math.abs(eList[i][inp]) < 10E-5) {//&&eList[i][inp] add
                strE = strE + 0 + ",";
            } else {
                strE = strE + eList[i][inp] + ",";
            }
        }
        return (strE);
    }

    public int getNpt() {
        return (npt);
    }

    public int getAindex(int i) {
        return (aindex[i]);
    }

    public double getA(int i) {
        return (a[i]);
    }

    public double[] getA() {//vj-2013-03-25
        return (a);
    }

    public void getA(double[] aIn) {//vj-2013-04-09
        System.arraycopy(a, 0, aIn, 0, a.length);
    }

    public boolean getIa(int i) {
        return (ia[i]);
    }

    public void getIa(boolean[] iaIn) {//vj-2013-04-09
        System.arraycopy(ia, 0, iaIn, 0, ia.length);
    }

    public void getDyda(double[] dydaOut) {//vj-2013-04-01
        System.arraycopy(dyda, 0, dydaOut, 0, dyda.length);
    }

    public int getMaxFitItr() {//vj-2013-05-07
        return (maxFitItr);
    }

    public void getStrOpt(String[][] strIn) {//vj-2013-04-01
//        strIn=this.strOpt;
        for (int itr = 0; itr < maxFitItr; itr++) {
            System.arraycopy(strOpt[itr], 0, strIn[itr], 0, strOpt[itr].length);
        }
    }

    public PHASEBINCE genPhase(String[] pid) throws IOException {
        //Print.f(cname + ".genPhase called", 7);
        String pType = pid[0];
        String pModel = pid[1];//vj-2013-05-19
        String pInstance = pid[2];//vj-2013-05-19
        Print.f("PhaseData.GenPhase() method called with pType:" + pType + " and pModel:" + pModel + " and pInstance:" + pInstance, 7);
        int pIndex = getPid(pType, pModel, pInstance);//get pIndex of phaseData by matching pType,pModel and pInstance of exptData
        PHASEBINCE phase_local = null;
        double T_in = 1500.0; //default initial temperature of the phase
        double xB_in = 0.5; //default initial composition of the phase
     
        switch (pType) {
            case "A1": {
                switch (pModel) {
                    case "RK": {
                        phase_local = new RK(stdst[pIndex], eList[pIndex], T_in, xB_in);
                        break;
                    }
                    case "TO": {
                        phase_local = new A1TOBINCE(stdst[pIndex], eList[pIndex], eMatFileName[pIndex], mList[pIndex], T_in, xB_in);
                        break;
                    }
                    case "QT": {
                        phase_local = new A1QTBINCE(stdst[pIndex], eList[pIndex], eMatFileName[pIndex], mList[pIndex], T_in, xB_in);
                        break;
                    }
                    default: {
                        phase_local = new A1TOBINCE(stdst[pIndex], eList[pIndex], eMatFileName[pIndex], mList[pIndex], T_in, xB_in);
                        break;
                    }
                }
                break;
            }

            case "L10": {
                switch (pModel) {
                    case "TO": {
                        phase_local = new L10TOBINCE(stdst[pIndex], eList[pIndex], eMatFileName[pIndex], mList[pIndex], T_in, xB_in);
                    }
                }
                break;
            }
            case "L12": {
                switch (pModel) {
                    case "TO": {
                        phase_local = new L12TOBINCE(stdst[pIndex], eList[pIndex], eMatFileName[pIndex], mList[pIndex], T_in, xB_in);
                    }
                }
                break;
            }
            case "A2": {
                switch (pModel) {
                    case "RK":
                        Print.f("stdst[pIndex]", stdst[pIndex], 0);
                        phase_local = new RK(stdst[pIndex], eList[pIndex], T_in, xB_in);
                        //phase_local.printPhaseInfo();
                        break;
                    case "T":
                        phase_local = new A2TBINCE(stdst[pIndex], eList[pIndex], eMatFileName[pIndex], mList[pIndex], T_in, xB_in);//vj-15-03-11
                        break;
                    case "ORC":
                        phase_local = new A2ORCBINCE(stdst[pIndex], eList[pIndex], eMatFileName[pIndex], T_in, xB_in);
                        break;
                    default: {
                        phase_local = new A2TBINCE(stdst[pIndex], eList[pIndex], eMatFileName[pIndex], mList[pIndex], T_in, xB_in);
                        break;
                    }
                }
                break;
            }
            case "B2": {
                switch (pModel) {
                    case "T":
                        phase_local = new B2TBINCE(stdst[pIndex], eList[pIndex], eMatFileName[pIndex], mList[pIndex], T_in, xB_in);
                        break;
                }
                break;
            }
            case "A3": {
                switch (pModel) {
                    case "RK":
                        phase_local = new RK(stdst[pIndex], eList[pIndex], T_in, xB_in);
                        break;
                    case "TO": {
                        phase_local = new A3TOBINCE(stdst[pIndex], eList[pIndex], eMatFileName[pIndex], mList[pIndex], T_in, xB_in);
                        break;
                    }
                }
                break;
            }
            case "B19": {
                switch (pModel) {
                    case "TO": {
                        phase_local = new B19TOBINCE(stdst[pIndex], eList[pIndex], eMatFileName[pIndex], mList[pIndex], T_in, xB_in);
                        break;
                    }
                }
                break;
            }
            case "D019": {
                switch (pModel) {
                    case "TO": {
                        phase_local = new D019TOBINCE(stdst[pIndex], eList[pIndex], eMatFileName[pIndex], mList[pIndex], T_in, xB_in);
                        break;
                    }
                }
                break;
            }
            case "L": {
                phase_local = new RK(stdst[pIndex], eList[pIndex], T_in, xB_in);
                break;
            }
            case "SC": {
                phase_local = new STCOMP(stdst[pIndex], eList[pIndex], T_in, xB_in);
                break;
            }
            default: {
                Print.f("PhaseData.GenPhase(): Phase does not exist/to be updated in PhaseData.GenPhase method", 0);
                break;
            }
        }
        //phase_local.printPhaseInfo();
        Print.f(cname + ".GenPhase ended", 7);
        return (phase_local);
    }

    public void setStrOpt(double chisq, double alamda, double[] a, double[] beta, double[] da, int mfit, int i) throws IOException {
        String eot = "\t";
        DecimalFormat df1 = new DecimalFormat("#.000");
        DecimalFormat df2 = new DecimalFormat("#0.000000");
        strOpt[i][0] = Integer.toString(i) + eot;
        strOpt[i][1] = df1.format(chisq) + eot;
        strOpt[i][2] = df2.format(alamda) + eot;
        setStr(i, 3, a);
        setStr(i, 4, beta, mfit);
        setStr(i, 5, da, mfit);
    }

    private void setStr(int Iter_In, int index_In, double[] ar_In) throws IOException {
        DecimalFormat df = new DecimalFormat("#0.000");
        String str = "{", eot = ",";
        for (int i = 0; i < ar_In.length - 1; i++) {
            if (((int) (ar_In[i]) == 0) && (!getIa(i))) {
                str += 0 + eot;
            } else {
                str += df.format(ar_In[i]) + eot;
            }
        }
        if (((int) (ar_In[ar_In.length - 1]) == 0) && (!getIa(ar_In.length - 1))) {
            str += 0;
        } else {
            str += df.format(ar_In[ar_In.length - 1]);
        }
        strOpt[Iter_In][index_In] = str + "}" + "\t";
    }

    private void setStr(int Iter_In, int index_In, double[] ar_In, int itr) throws IOException {
        DecimalFormat df = new DecimalFormat("#0.000000");
        String str = "{", eot = ",";
        for (int i = 0; i < itr - 1; i++) {
            str += df.format(ar_In[i]) + eot;
        }
        str += df.format(ar_In[itr - 1]);
        strOpt[Iter_In][index_In] = str + "}" + "\t";
    }

    public int getPid(String pType, String pModel, String pInstance) throws IOException {//vj-2013-05-19-Modified
        Print.f(cname + ".getpIndex() method called", 7);
        for (int i = 0; i < nPhases; i++) {
            if ((pidList[i][0].equalsIgnoreCase(pType)) && (pidList[i][1].equalsIgnoreCase(pModel)) && (pidList[i][2].equalsIgnoreCase(pInstance))) {
                Print.f(cname + ".getpIndex() method ended with pIndex:" + i, 7);
                return (i);
            }
        }//2012=02-16(VJ): Modified
        Print.f("calbince.Phasedata.getPid(): Error! No matching phase description is found in phasedata", 1);
        return (-1);
    }

    public void getPhaseParamaters(String pType_in, String pModel_in, String pInstance_in, Object[] phaseParameters) throws IOException {
        Print.f(cname + ".getPhaseParamaters() method called", 7);
        int pIndex;
        pIndex = getPid(pType_in, pModel_in, pInstance_in);
        //Object[] phaseParameters = new Object[4];
        String[] stdst_out = new String[stdst[pIndex].length];
        double[] elist_out = new double[eList[pIndex].length];
        String eMatFileName_out;
        double[] mlist_out;

        System.arraycopy(stdst[pIndex], 0, stdst_out, 0, stdst[pIndex].length);
        //elist_out = eList[pIndex];
        System.arraycopy(eList[pIndex], 0, elist_out, 0, eList[pIndex].length);
        eMatFileName_out = eMatFileName[pIndex];
        mlist_out = mList[pIndex];
        phaseParameters[0] = stdst_out;
        phaseParameters[1] = elist_out;
        phaseParameters[2] = eMatFileName_out;
        phaseParameters[3] = mlist_out;
        Print.f("PhaseData.getPhaseParamaters() method returned stdst:", stdst_out, 1);
    }

    //This Method combines EmatLists of the all the phases in to one Transmat matrix, to be used for transforming parameter list 'a' to 'aTrans'
    public void calTransMat(double[][] transMat) throws IOException {
        Print.f(cname + ".transMat method called", 5);
        int nPhase = getNumPhases();
        int jj = 0, kk = 0;
        int[] index = new int[nPhase];
        double[][][] emat = new double[nPhase][][];
        for (int i = 0; i < nPhase; i++) {
            index[i] = getNp(i);
        }
        for (int i = 0; i < nPhase; i++) {
            if (isLinkedPID(i)) {
                emat[i] = new double[getNp(i)][getNp(i)];
                emat[i] = getEMatList(i);
                for (int j = 0; j < index[i]; j++) {

                    System.arraycopy(emat[i][j], 0, transMat[j + kk], kk, index[i]);
                    jj = jj + 1;
                }
                kk = jj;
            }
        }
        Print.f(cname + ".transMat method ended", 5);
    }

    public void print() throws IOException {//vj-2012-03-25
        Print.f(cname + ".print method called", 7);
        Utils.drawLine();
        int logLevel = 0;
        Print.f("Phase File:" + phaseFileName, logLevel);
        Print.f("No of Phases:" + nPhases, logLevel);
        for (int iphase = 0; iphase < getNumPhases(); iphase++) {
            Print.f("", logLevel);
            Print.f("PhaseIndex     :" + "\t" + (int) getPhaseIndex(iphase), logLevel);
            Print.f("Pid            :" + "\t" + getPid0(iphase) + "-" + getPid1(iphase) + "-" + getPid2(iphase), logLevel);
            Print.f("Linked PID     :" + "\t" + getLinkedPid0(iphase) + "-" + getLinkedPid1(iphase) + "-" + getLinkedPid2(iphase), logLevel);
            Print.f("Components     :" + "\t" + getComponentA(iphase) + "\t" + getComponentB(iphase), logLevel);
            Print.f("Stdst database :" + "\t" + getStdst(iphase)[3], logLevel);
            Print.f("No of Parameters :" + "\t" + getNp(iphase), logLevel);
            Print.f("Multiplicity   :" + "\t" + getStrM(iphase), logLevel);
            Print.f("IaList         :" + "\t" + getStrIa(iphase), logLevel);
            Print.f("ECIs           :" + "\t" + getStrE(iphase), logLevel);
            Print.f("EMatFileName   :" + "\t" + getEMatFileName(iphase), logLevel);
            //Print.f("emat", eMatList[iphase], 0);
            Print.f("", logLevel);
        }
        Utils.drawLine();
        Print.f(cname + ".print method executed", 7);
    }

    public void printFitParam() throws IOException {
        Print.f(cname + ".printFitParam() method called", 7);
        Utils.drawLine();
        int logLevel = 0;
        Print.f("No of Optimization Parameters:" + npt, logLevel);
        Print.f("aindex", aindex, logLevel);
        Print.f("a", a, logLevel);
        Print.f("ia", getStr(ia), logLevel);
        Utils.drawLine();
        Print.f(cname + ".printFitParam() method executed", 7);
    }
}
