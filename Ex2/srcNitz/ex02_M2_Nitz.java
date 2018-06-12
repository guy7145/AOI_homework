

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import parser.JSONParser;
import defpack.*;

public class ex02_M2_Nitz {

    public static void main(String[] args) throws IOException {
       /* PrintWriter csv = new PrintWriter("tracesfunction.txt", "UTF-8");

        for(int i=1600;i<3000;i=i+20) {
            try {
                Runner.key="";
                Runner.RECORDED_TRACES_NUMBER = i;
                String res = Runner.main2(args);
                int myres = comp(res, "bfd6e9be3e4afa21f579d178652a30e9");
                csv.append(i + "," + myres+"\n");
                csv.flush();
                System.out.println(i + "," + myres);
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        csv.close();
    	*/
       //
        Runner.main2(args);

    }

    private static int comp(String res, String mst) {
        int counter=0;
        for(int i=0;i<Math.min(mst.length(),res.length());i++)
        {
            if(res.charAt(i)==mst.charAt(i))
                counter++;
        }
        return counter;
    }
}