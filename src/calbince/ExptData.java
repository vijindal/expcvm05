/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package calbince;

import binutils.io.Print;
import binutils.stat.Utils;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author admin
 */
public class ExptData {
    //Experimental data

    private String dataFileName = null;//vj-2012-03-25
    private int ndat = 0;//vj-2012-03-25-No of Data Points
    private double dataIndex[] = null; // SU: added 20.03.2012
    private String dataType[] = null;//vj-2012-03-25
    private String method[] = null;//for storing calculation method/boundary type e.g. MGT1, MGX1...
    private String pList[][][] = null;//for storing phases for each data, maximum three phases with its name and index
    private double y[] = null;
    private double ymod[] = null;
    private double sig[] = null;
    private double wt[] = null;
    private double x[];
    private double x1[] = null;
    private double x2[] = null;
    private String ref[] = null;//vj-2012-03-23
    private final String cname = "ExptData";//vj-2013-05-18

    public ExptData() throws IOException {
        Print.f(cname + ".constructor called", 6);
        Print.f(cname + ".constructor closed", 6);
    }

    public void init(String dataFileNameIn, int ndatIn) throws IOException {
        Print.f(cname + ".init method called", 7);
        dataFileName = dataFileNameIn;
        ndat = ndatIn;
        method = new String[ndat];
        pList = new String[ndat][3][3];//vj-2013-05-19-Increased one dimension for storing model also.
        dataIndex = new double[ndat];
        for (int i = 0; i < ndat; i++) {
            dataIndex[i] = i;
        }
        dataType = new String[ndat];
        y = new double[ndat];
        ymod = new double[ndat];
        sig = new double[ndat];
        wt = new double[ndat];
        x = new double[ndat];
        x1 = new double[ndat];
        x2 = new double[ndat];
        ref = new String[ndat];
        Print.f(cname + ".init method closed", 7);
    }

    public void init(String dataFileNameIn, ExptData expdataIn) throws IOException {//vj-2013-06-02
        Print.f(cname + ".init method called", 7);
        dataFileName = dataFileNameIn;
        ndat = expdataIn.getNdat();
        method = new String[ndat];
        pList = new String[ndat][3][3];//vj-2013-05-19-Increased one dimension for storing model also.
        dataIndex = new double[ndat];
        for (int i = 0; i < ndat; i++) {
            dataIndex[i] = expdataIn.getDataIndex(i);
        }
        dataType = new String[ndat];
        y = new double[ndat];
        ymod = new double[ndat];
        sig = new double[ndat];
        wt = new double[ndat];
        x = new double[ndat];
        x1 = new double[ndat];
        x2 = new double[ndat];
        ref = new String[ndat];
        Print.f(cname + ".init method closed", 7);
    }

    //Setter Methods
    public void setdataIndex(double xIn, int i) {
        dataIndex[i] = xIn;
    }

    public void setDataType(String xIn, int i) {
        dataType[i] = xIn;
    }

    public void setBList(String xIn, int i) {
        method[i] = xIn;
    }

    public void setPid(String xIn, int i, int j, int k) {
        pList[i][j][k] = xIn;
    }

    public void setY(double xIn, int i) {
        y[i] = xIn;
    }

    public void setYmod(double xIn, int i) {
        ymod[i] = xIn;
    }

    public void setSig(double xIn, int i) {
        sig[i] = xIn;
    }

    public void setWt(double xIn, int i) {
        wt[i] = xIn;
    }

    public void setX(double xIn, int i) {
        x[i] = xIn;
    }

    public void setX1(double xIn, int i) {
        x1[i] = xIn;
    }

    public void setX2(double xIn, int i) {
        x2[i] = xIn;
    }

    public void setRefList(String xIn, int i) {
        ref[i] = xIn;
    }

    public void setVoid() throws IOException {//vj-2013-05-18
        Print.f(cname + ".setVoid() method called", 7);
        dataFileName = null;
        ndat = 0;
        method = null;
        pList = null;
        dataIndex = null;
        dataType = null;
        y = null;
        ymod = null;
        sig = null;
        wt = null;
        x = null;
        x1 = null;
        x2 = null;
        ref = null;
        Print.f(cname + ".setVoid() method closed", 7);
    }
    //Getter Methods

    public String getDataFileName() {
        return (dataFileName);
    }

    public int getNdat() {
        return (ndat);
    }

    public double getDataIndex(int i) {
        return (dataIndex[i]);
    }

    public String getDataType(int i) {
        return (dataType[i]);
    }

    public String getBList(int i) {
        return (method[i]);
    }

    public String[][][] getPid() {//vj-2013-03-23
        return (pList);
    }

    public String[][] getPid(int i) {//vj-2013-03-23
        return (pList[i]);
    }

    public String getPid(int i, int j, int k) {
        return (pList[i][j][k]);
    }

    public double getY(int i) {
        return (y[i]);
    }

    public double[] getY() {//vj-2013-03-24
        return (y);
    }

    public double getYmod(int i) {
        return (ymod[i]);
    }

    public double getSig(int i) {
        return (sig[i]);
    }

    public double[] getSig() {//vj-2013-03-24
        return (sig);
    }

    public double getWt(int i) {
        return (wt[i]);
    }

    public double[] getWt() {//vj-2013-03-31
        return (wt);
    }

    public double getX(int i) {
        return (x[i]);
    }

    public double[] getX() {//vj-2013-03-24
        return (x);
    }

    public double getX1(int i) {
        return (x1[i]);
    }

    public double getX2(int i) {
        return (x2[i]);
    }

    public String getRef(int i) {
        return (ref[i]);
    }

    public int nph(String pid) {//vj-2013-03-23-returns number of phases involved for the given thermodynamic/phase diagram calculation
        return (0);
    }

    public ExptDatum getExptDatum(int idat) throws IOException {
        ExptDatum exptdatum = new ExptDatum();
        exptdatum.setDataIndex(dataIndex[idat]);
        exptdatum.setDataType(dataType[idat]);
        exptdatum.setBList(method[idat]);
        exptdatum.setPid(pList[idat]);
        exptdatum.setY(y[idat]);
        exptdatum.setYmod(ymod[idat]);
        exptdatum.setSig(sig[idat]);
        exptdatum.setWt(wt[idat]);
        exptdatum.setX(x[idat]);
        exptdatum.setX1(x1[idat]);
        exptdatum.setX2(x2[idat]);
        exptdatum.setRef(ref[idat]);
        return (exptdatum);
    }

    public void setExptDatum(ExptDatum exptdatum) throws IOException {
        Print.f(cname + ".setExptDatum method called", 7);
        for (int i = 0; i < ndat; i++) {
            if (exptdatum.getdIndex() == dataIndex[i]) {
                this.method[i] = exptdatum.getBList();
                this.pList[i] = exptdatum.getPid();
                this.y[i] = exptdatum.getY();
                this.ymod[i] = exptdatum.getYmod();
                this.sig[i] = exptdatum.getSig();
                this.wt[i] = exptdatum.getWt();
                this.x[i] = exptdatum.getX();
                this.x1[i] = exptdatum.getX1();
                this.x2[i] = exptdatum.getX2();
                this.ref[i] = exptdatum.getRef();
            }
        }
        Print.f(cname + ".setExptDatum method ended", 7);
    }

    public void check(PhaseData phasedata) throws IOException {
        String [] phaseIndex=new String[3];
        if (phasedata == null) {
            Print.f(phasedata + " object not initialized", 0);
        } else {
            //countPhases();
        }
    }

    private void countPhases() throws IOException {
        List<String[]> p = new ArrayList<String[]>();
        for (int i = 0; i < ndat; i++) {
            for (int j=0;j<pList[i].length;j++) {
                Print.f("pList["+i+"][item]",pList[i][j],1);
                System.out.println(p.size());
                System.out.println((p.contains(pList[i][j])));
                if(!(p.contains(pList[i][j]))){
                    p.add( pList[i][j]);
                };
            }
        }
        System.out.println(Arrays.toString(p.get(0)));
        System.out.println(p.size());
    }

    public void print() throws IOException {//vj-2012-03-23
        DecimalFormat df = new DecimalFormat("#0.000000");
        Print.f(cname + ".print method called", 7);
        Utils.drawLine();
        int logLevel = 0;
        Print.f("Data File:" + dataFileName, logLevel);
        Print.f("No of Data Points:" + ndat, logLevel);
        for (int idata = 0; idata < ndat; idata++) {
            Print.f((int) dataIndex[idata] + "\t" + method[idata] + "\t" + pList[idata][0][0] + "-" + pList[idata][0][1] + "-" + pList[idata][0][2] + "\t" + pList[idata][1][0] + "-" + pList[idata][1][1] + "-" + pList[idata][1][2] + "\t" + pList[idata][2][0] + "-" + pList[idata][2][1] + "-" + pList[idata][2][2] + "\t" + df.format(y[idata]) + "\t" + df.format(ymod[idata]) + "\t" + df.format(sig[idata]) + "\t" + df.format(wt[idata]) + "\t" + df.format(x[idata]) + "\t" + df.format(x1[idata]) + "\t" + df.format(x2[idata]) + "\t" + ref[idata], logLevel);
        }
        Utils.drawLine();
        Print.f(cname + ".print method executed", 7);
    }

}
