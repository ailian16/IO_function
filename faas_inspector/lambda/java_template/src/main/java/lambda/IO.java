/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lambda;

import com.amazonaws.services.lambda.runtime.ClientContext;
import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import com.amazonaws.services.lambda.runtime.Context; 
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import faasinspector.register;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Random;
/**
 * lambda.IO::handleRequest
 * @author wlloyd
 * @author ailian
 */
public class IO implements RequestHandler<Request, Response>
{
    static String CONTAINER_ID = "/tmp/container-id";
    static Charset CHARSET = Charset.forName("US-ASCII");
    
     // Lambda Function Handler
    public Response handleRequest(Request request, Context context) {
        // Create logger
        LambdaLogger logger = context.getLogger();
        
        // Register function
        register reg = new register(logger);

        //stamp container with uuid
        Response r = reg.StampContainer();
        
        // *********************************************************************
        // Implement Lambda Function Here
        // *********************************************************************
        String hello = "No output";

        int numfiles = request.getNumfiles();
        String fileops = request.getFileops();
        int numfileops = request.getNumfileops();
        String optype = request.getOptype();
        String nodelete = request.getNodelete();
        
        switch (fileops) {
            case "SR":
                sequentialRead(numfiles, numfileops, optype);
                if(nodelete.equals("true")) {
                    delete(numfiles);
                }   break;
            case "RR":
                randomRead(numfiles, numfileops, optype);
                if(nodelete.equals("true")) {
                    delete(numfiles);
                }   break;
            case "W":
                writeFiles(numfiles, numfileops, optype);
                if(nodelete.equals("true")) {
                    delete(numfiles);
                }   break;
            case "TR":
                staticRead(numfiles, numfileops, optype);
                if(nodelete.equals("true")) {
                    delete(numfiles);
                }   break;
            default:
                try
                {
                    Thread.sleep(request.getSleep());
                }
                catch (InterruptedException ie)
                {
                    System.out.println("Sleep was interrupted - no calc mode...");
                }   break;
        }
         
        
        //Print log information to the Lambda log as needed
        logger.log("log message...");
        
        // Set return result in Response class, class is marshalled into JSON
        r.setValue(hello);
        reg.setRuntime();
        return r;
    }
    
    private void writeFiles(int numfiles, int numfileops, String optype) {
        // TODO Auto-generated method stub
        for (int i = 0; i < numfiles; i++) {
            try {
                String filename = String.valueOf(i + 1) + ".txt";
                filename = "/tmp/" + filename;
                PrintWriter writer = new PrintWriter(filename, "UTF-8");
                for(int j = 0; j < numfileops; j++) {
                    if(optype.equals("L")) {
                        //random write a string of provided length
                        writer.println(randomLine(80));
                    }else if(optype.equals("B")){
                        Random r = new Random();
                        int index = r.nextInt(26);
                        char cur = (char)('a' + r.nextInt(26));
                        writer.print(cur);
                    }else {
                        System.out.println("Invalid input of 'optype'!");
                    }
                }
                writer.close();
            }catch (IOException e){
                e.printStackTrace();
            }

        }   
    }

    private void delete(int numfiles) {
        //delete
        for (int i = 0; i < numfiles; i++) {
            String filename = String.valueOf(i + 1) + ".txt";
            filename = "/tmp/" + filename;
            File file = new File(filename);
            file.delete();
        }
    }

    private void sequentialRead(int numfiles, int numfileops, String optype) {
        writeFiles(numfiles, numfileops, optype);
        for (int i = 0; i < numfiles; i++) {
            String filename = String.valueOf(i + 1) + ".txt";
            filename = "/tmp/" + filename;
            for(int j = 0; j < numfileops; j++) {      
                try{
                    BufferedReader in = new BufferedReader(new FileReader(filename));
                    System.out.println("Reading: " + filename + " now...");
                    if(optype.equals("L")) {
                        //random?
                        while (in.readLine() !=null){
                            continue;
                        }
                        in.close();
                    }else if(optype.equals("B")){
                        while(in.read() != -1) {
                           continue;        
                        }
                        in.close();
                    }else {
                        System.out.println("Invalid input of 'optype'!");
                    }
                }catch(IOException e) {
                    e.printStackTrace();
                } 
                
            }
        }   
    }

    private void staticRead(int numfiles, int numfileops, String optype){
        writeFiles(numfiles, numfileops, optype);
        Random r = new Random();
        int index = r.nextInt(numfileops) + 1;
        System.out.println("index: " + index);
        
        for (int i = 0; i < numfiles; i++) {
            String filename = "/tmp/" + String.valueOf(i + 1) + ".txt";
            File file = new File(filename); 
            System.out.println("Reading: " + filename + " now...");           
            try{
                BufferedReader reader = new BufferedReader(new FileReader(filename));
                if(optype.equals("L")){
                    //static readom read -line
                    reader.mark(numfileops*80 + 80);
                    for(int j = 0; j < numfileops; j++){
                        String line = reader.readLine();
                        for(int k = 0; k < index; k++){
                            if(line == null){
                                reader.reset();
                                reader.readLine();
                            }
                            line = reader.readLine();
                            if(line == null){
                                reader.reset();
                                line = reader.readLine();
                            }
                        }
                        System.out.println(line);
                    }
                    reader.reset();
                    reader.close();

                }else if(optype.equals("B")){
                    //static random read - byte
                    reader.mark(numfileops + 1);
                    for(int j = 0; j < numfileops; j++){
                        int c = reader.read();
                        for(int k = 0; k < index; k++){                            
                            if(c == -1){
                                reader.reset();
                                reader.read();
                            }
                            c = reader.read();
                            if(c == -1){
                                reader.reset();
                                c = reader.read();
                            }
                        } 
                    }
                    reader.reset();
                    reader.close();
                }else {
                    System.out.println("Invalid input of 'optype'!");
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }
    
    private void randomRead(int numfiles, int numfileops, String optype) {
        writeFiles(numfiles, numfileops, optype);
        
        for (int i = 0; i < numfiles; i++) {
            String filename = String.valueOf(i + 1) + ".txt";
            filename = "/tmp/" + filename;
            File file = new File(filename); 
            long len = file.length();
            try {                
                for(int j = 0; j < numfileops; j++) { 
                    System.out.println("Reading: " + filename + " now...");                    
                    if(optype.equals("L")) {
                        String line="";
                        BufferedReader reader = new BufferedReader(new FileReader(filename));
                        Random r = new Random();
                        int index = r.nextInt(numfileops);                        
                        while(index != 0){
                            reader.readLine();
                            index--;
                        }                        
                        line = reader.readLine();
                        reader.close();
                    }else if(optype.equals("B")){
                        BufferedReader reader = new BufferedReader(new FileReader(filename)); 
                        Random r = new Random();
                        long randomNum = (long)(r.nextDouble()*len);
                        int c = 0;
                        reader.skip(randomNum);
                        while((c = reader.read()) != -1) {
                            char character = (char) c;          
                            System.out.print(character);          
                        } 

                    }else {
                        System.out.println("Invalid input of 'optype'!");
                    }
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    private String randomLine(int length) {
        String str = "";
        for(int i = 0; i < length; i++) {
            Random r = new Random();
            int index = r.nextInt(26);
            char cur = (char)('a' + r.nextInt(26));
            str += cur;
        }
        return str;
    }
    
    // int main enables testing function from cmd line
    public static void main (String[] args)
    {
        if ((args == null) || (args.length == 0))
        {
            System.out.println("Usage arguments:");
            System.out.println("1 - total number of files");
            System.out.println("2 - Type of operations parameter: SR-sequential-read, RR-random-read, TR- Static Read,  W-write");
            System.out.println("3 - Number of ops to perform against a file.  An op is reading or writing a byte or line to a file");
            System.out.println("4 - L-read/write lines, B-read/write bytes");
            System.out.println("5 - true/false (indicates whether files created should be deleted at the end of the Lambda function)");
            System.out.println("6 - time of sleep");
//            return;
        }
        
        Context c = new Context() {
            @Override
            public String getAwsRequestId() {
                return "";
            }

            @Override
            public String getLogGroupName() {
                return "";
            }

            @Override
            public String getLogStreamName() {
                return "";
            }

            @Override
            public String getFunctionName() {
                return "";
            }

            @Override
            public String getFunctionVersion() {
                return "";
            }

            @Override
            public String getInvokedFunctionArn() {
                return "";
            }

            @Override
            public CognitoIdentity getIdentity() {
                return null;
            }

            @Override
            public ClientContext getClientContext() {
                return null;
            }

            @Override
            public int getRemainingTimeInMillis() {
                return 0;
            }

            @Override
            public int getMemoryLimitInMB() {
                return 0;
            }

            @Override
            public LambdaLogger getLogger() {
                return new LambdaLogger() {
                    @Override
                    public void log(String string) {
                        System.out.println("LOG:" + string);
                    }
                };
            }
        };
        
        // Create an instance of the class
        IO lt = new IO();
        
        // Create a request object
        Request req = new Request();
        
        // Grab the name from the cmdline from arg 0
        int numfiles = (args.length > 0 ? Integer.parseInt(args[0]) : 100000);
        String fileops = (args.length > 1 ? args[1] : "W");
        int numfileops = (args.length > 2 ? Integer.parseInt(args[2]) : 100);
        String optype = (args.length > 3 ? args[3] : "B");
        String nodelete = (args.length > 4 ? args[4] : "true");
        int sleep = (args.length > 5 ? Integer.parseInt(args[5]) : 0);
        
        // Load the name into the request object
        req.setNumfiles(numfiles);
        req.setFileops(fileops);
        req.setNumfileops(numfileops);
        req.setOptype(optype);
        req.setNodelete(nodelete);
        req.setSleep(sleep);

        // Report name to stdout
        System.out.println("cmd-line numfiles=" + req.getNumfiles() + " fileops=" + req.getFileops() + " numfileops=" + req.getNumfileops() + " optype=" + req.getOptype() + " nodelete=" + req.getNodelete());
        
        // Run the function
        Response resp = lt.handleRequest(req, c);
        
        // Print out function result
        System.out.println("function result:" + resp.toString());
    }
}

