import java.io.*;
import java.net.*;
import java.util.*;

import java.io.FileReader; 
import java.io.File;  // Import the File class
import java.io.IOException;  // Import the IOException class to handle errors
//import java.util.Iterator; 
//import java.util.Map; 


//import org.json.simple.JSONArray; 
//import org.json.simple.JSONObject; 
//import org.json.simple.parser.*;


class server {

    public static void main(String argv[]) throws Exception {

        if(argv.length < 1)
            throw new IllegalArgumentException("Port argument missing.");

        String requestMessageLine;
        String fileName;

        ServerSocket listenSocket = new ServerSocket(Integer.parseInt(argv[0]));
        Socket connectionSocket = listenSocket.accept();

        BufferedReader inFromClient =
            new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
        DataOutputStream outToClient =
            new DataOutputStream(connectionSocket.getOutputStream());

        String confname = "/tmp/myhttpd.conf";
        File conf = new File(confname);
        int confBytes = (int) conf.length();
        FileInputStream confInFile = new FileInputStream(confname);
        byte[] confInBytes = new byte[confBytes];
        confInFile.read(confInBytes);
        String root = new String(confInBytes).split("\n")[0].split(" ")[1];
        //System.out.println(root);

        requestMessageLine = inFromClient.readLine();
        StringTokenizer tokenizedLine =
            new StringTokenizer(requestMessageLine);
        String received = tokenizedLine.nextToken();
        fileName = tokenizedLine.nextToken();
        String version = tokenizedLine.nextToken();
        switch (received) { 
                case "HEAD": 
                    if(fileName.equals("/"))
                        fileName = root;
                    if (fileName.startsWith("/") == true)
                            fileName = fileName.substring(1);
                        else{
                            outToClient.writeBytes(version + " 400 INVALID FORMAT");
                            break;
                        }
                          try{
                    fileName = "/var/www/html/"+fileName;
                    File headFile = new File(fileName);
                    if(!headFile.isFile()){
                        outToClient.writeBytes(version + " 404 FILE NOT FOUND");
                        break;
                    }
                    int headNumOfBytes = (int) headFile.length();
                    FileInputStream headInFile = new FileInputStream(fileName);
                    byte[] headFileInBytes = new byte[headNumOfBytes];
                    headInFile.read(headFileInBytes);
                    outToClient.writeBytes(version + " 200 OK\r\n");
                    if(fileName.contains(".")){
                        String contentType = fileName.substring(fileName.indexOf('.'));
                        switch(contentType){
                            case ".txt":
                                outToClient.writeBytes("Content-Type: text/plain\r\n");
                                break;
                            case ".html":
                                outToClient.writeBytes("Content-Type: text/html\r\n");
                                break;
                            case ".jpeg":
                                outToClient.writeBytes("Content-Type: image/jpeg\r\n");
                                break;
                            case ".png":
                                outToClient.writeBytes("Content-Type: image/png\r\n");
                                break;
                            case ".json":
                                outToClient.writeBytes("Content-Type: application/json\r\n");
                                break;
                            default:
                                outToClient.writeBytes("Content-Type: multipart/form-data\r\n");
                                break;
                                }
                            } else outToClient.writeBytes("Content-Type: other/other\r\n");
                            outToClient.writeBytes("Content-Length: " + headNumOfBytes + "\r\n");
                            outToClient.writeBytes("\r\n");
                                connectionSocket.close();
                            break;
                            }
                            catch(Exception e){
                                outToClient.writeBytes(version + " 403 NO READ PERMISSIONS");
                                break;
                            }
                case "GET":
                    if(fileName.equals("/"))
                        fileName = root; 
                    if (fileName.startsWith("/") == true)
                        fileName = fileName.substring(1);
                    else{
                        outToClient.writeBytes(version + " 400 INVALID FORMAT");
                        break;
                    }
                    try{
                    fileName = "/var/www/html/"+fileName;
                    File file = new File(fileName);
                    if(!file.isFile()){
                        outToClient.writeBytes(version + " 404 FILE NOT FOUND");
                        break;
                    }
                    int numOfBytes = (int) file.length();
                    FileInputStream inFile = new FileInputStream(fileName);
                    byte[] fileInBytes = new byte[numOfBytes];
                    inFile.read(fileInBytes);
                    outToClient.writeBytes(version + " 200 OK\r\n");
                    if(fileName.contains(".")){
                        String contentType = fileName.substring(fileName.indexOf('.'));
                        switch(contentType){
                            case ".txt":
                                outToClient.writeBytes("Content-Type: text/plain\r\n");
                                break;
                            case ".html":
                                outToClient.writeBytes("Content-Type: text/html\r\n");
                                break;
                            case ".jpeg":
                                outToClient.writeBytes("Content-Type: image/jpeg\r\n");
                                break;
                            case ".png":
                                outToClient.writeBytes("Content-Type: image/png\r\n");
                                break;
                            case ".json":
                                outToClient.writeBytes("Content-Type: application/json\r\n");
                                break;
                            default:
                                outToClient.writeBytes("Content-Type: multipart/form-data\r\n");
                                break;
                        }
                    } else outToClient.writeBytes("Content-Type: other/other\r\n");
                    outToClient.writeBytes("Content-Length: " + numOfBytes + "\r\n");
                    outToClient.writeBytes("\r\n");
                    outToClient.write(fileInBytes, 0, numOfBytes);
                    connectionSocket.close();
                    break;
                    }
                    catch(Exception e){
                        outToClient.writeBytes(version + " 403 NO READ PERMISSIONS");
                        break;
                    }

                case "POST":
                    //fileName = tokenizedLine.nextToken();
                    try {
                        String header = inFromClient.readLine();
                        if(!header.contains("Content-Length:")){
                            outToClient.writeBytes(version + " 400 MISSING HEADER");
                            break;
                        }
                        fileName = "/var/www/writefiles/"+fileName;
                        FileWriter myWriter = new FileWriter(fileName);
                        outToClient.writeBytes(version + " 201 FILE CREATED SUCCESSFULLY\r\n");
                        myWriter.close();
                        File postfile = new File(fileName);
                        int PostNumOfBytes = (int) postfile.length();
                        FileInputStream postInFile = new FileInputStream(fileName);
                        byte[] postFileInBytes = new byte[PostNumOfBytes];
                        postInFile.read(postFileInBytes);
                        outToClient.write(postFileInBytes, 0, PostNumOfBytes);
                        connectionSocket.close();
                        break; 
                    } catch (IOException e) {
                    System.out.println("An error occurred.");
                    e.printStackTrace();
                    break; 
                    }
                default:
                    outToClient.writeBytes(version + "  501 METHOD NOT IMPLEMENTED");
                    break;


        } 

    }
}
