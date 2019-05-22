/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lambda;

/**
 *
 * @author wlloyd
 */
public class Request {

    int numfiles;
    String fileops;
    int numfileops;
    String optype;
    String nodelete;
    int sleep;

    public int getNumfiles(){
        return numfiles;
    } 
    public void setNumfiles(int numfiles)
    {
        this.numfiles = numfiles;
    }
    public int getNumfileops()
    {
        return numfileops;
    }
    public void setNumfileops(int numfileops)
    {
        this.numfileops = numfileops;
    }
    public String getFileops()
    {
        return fileops;
    }
    public void setFileops(String fileops)
    {
        this.fileops = fileops;
    }
    public String getOptype()
    {
        return optype;
    }
    public void setOptype(String optype)
    {
        this.optype = optype;
    }
    public String getNodelete()
    {
        return nodelete;
    }
    public void setNodelete(String nodelete)
    {
        this.nodelete = nodelete;
    }
    public int getSleep()
    {
        return sleep;
    }
    public void setSleep(int sleep)
    {
        this.sleep = sleep;
    }
    public Request(int numfiles, String fileops, int numfileops, String optype, 
            String nodelete, int sleep)
    {
        this.numfiles = numfiles;
        this.fileops = fileops;
        this.numfileops = numfileops;
        this.optype = optype;
        this.nodelete = nodelete;
        this.sleep = sleep;
    }
    public Request()
    {
        
    }
}
