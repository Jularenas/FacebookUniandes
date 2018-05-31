package facebookUniandes;

import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
public class Main {
	
	private String token;
	
	private int[] likesPorHora;
	private int[] commentsPorHora;
	//1424071031165005,..
	//confesiones,IAYC,arquiDis,decanatura,viviendaUniversitaria,culturaUniandes,administracionUniandes,BibliotecaUniandes,academcioCEU,medicinaUniandes
	private String []pages={"","635091926628905","453864577993235","352563524830861","588480231191039","1805228329757023","379341528792135","141441056056913","250051698485664","108619413135707","1446939265541992"};
	
	public String[] getPages() {
		return pages;
	}
	public void setPages(String[] pages) {
		this.pages = pages;
	}
	
	public int[] getCommentsPorHora() {
		return commentsPorHora;
	}
	public void setCommentsPorHora(int[] commentsPorHora) {
		this.commentsPorHora = commentsPorHora;
	}
	public Main(String token){
		this.token=token;
		likesPorHora= new int[24];
		commentsPorHora= new int[24];
		for (int i = 0; i < likesPorHora.length; i++) {
			likesPorHora[i]=0;
		}
	}
	public void addLikes(int i,int val)
	{
		likesPorHora[i]+=val;
	}
	public void addComments(int i,int val)
	{
		commentsPorHora[i]+=val;
	}
	
	
	public int[] getLikesPorHora() {
		return likesPorHora;
	}
	public void setLikesPorHora(int[] likesPorHora) {
		this.likesPorHora = likesPorHora;
	}
	public URL crearSolicitudAlAPI(String id_page ) throws MalformedURLException{
		URL url = new URL("https://graph.facebook.com/v2.11/" +  id_page +"/posts?format=json&access_token=" + token);
		//		System.out.println(url);
		return url;
	}
	public String enviarSolicitud(URL url) throws IOException{
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setDoOutput(true);
		connection.connect();

		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String resultado = new String();
		String line;
		while ((line = reader.readLine()) != null) {
			resultado += line + "\n";
		}
		connection.disconnect();
		System.out.println(resultado);
		return resultado;

	}
	public URL crearSolicitudReacciones(String id_post) throws MalformedURLException
	{
		URL url= new URL("https://graph.facebook.com/v2.11/"+id_post+"/reactions?summary=total_count&access_token=" + token);
		return url;
	}
	public URL crearSolicitudComentarios(String id_post) throws MalformedURLException
	{
		URL url= new URL("https://graph.facebook.com/v2.11/"+id_post+"/comments?summary=1&access_token="+token);
		return url;
	}
	
	public static void main(String[] args)
	{
		Scanner s= new Scanner(System.in);
		try(PrintWriter op= new PrintWriter(new FileWriter(new File("./data/responses")));PrintWriter op2= new PrintWriter(new FileWriter(new File("./data/info.csv"))))
		{
			String token=s.nextLine();
			Main m= new Main(token);
			String []pags=m.getPages();
			for (int k = 0; k < pags.length; k++) {
//			while(true){
				
//				String id_page=s.nextLine();
				String id_page=pags[k];
//				op.println(token);
//				op.println(id_page);
//				op.println("------------------------");
//				op.println("------------------------");
//				op.println("------------------------");
				//EAACEdEose0cBAGd10wBgwaUMPoUkApTQg1aabSll2VMDwkR3NZByAY8QAKLgVu3GmkZBRtgyuapRHCR0YXJbhd3QeWZAcjKj2ML9cYiOIav47hy0QbiJ2UpodCbTd9UZCk88RFWCiIKBIsLKHMuBEZBTTbTv4bdIGZCOTQRhE2o9NOwDtfmqkQunT5fpuwTU0ZD
				//913494512115899
//				op.println("------------------------");
				
				try {
					String response=m.enviarSolicitud(m.crearSolicitudAlAPI(id_page));
					System.out.println("parsear");
					JSONParser parser= new JSONParser();
					Object obj=parser.parse(response);
					JSONObject object=(JSONObject)obj;
					JSONArray array=(JSONArray)object.get("data");
					Iterator<JSONObject>iter=array.iterator();
					while(iter.hasNext())
					{
						JSONObject post= new JSONObject(); 
						//System.out.println("iterData");
						JSONObject toParse=iter.next();
						//Object obj1=parser.parse("");
						//System.out.println(toParse);
						JSONObject object1=(JSONObject)toParse;
						
						String message=(String)object1.get("message");
						
						String id=(String)object1.get("id");
						String fecha=(String)object1.get("created_time");
						String time=fecha.split("T")[1];
						String timeSub=time.substring(0,2);
						int index=Integer.parseInt(timeSub);
						System.out.println(index);
						
						
						//
						//reactions
						//
						String response2=m.enviarSolicitud(m.crearSolicitudReacciones(id));
						Object obj3=parser.parse(response2);
						JSONObject object3=(JSONObject)obj3;
						JSONObject object4=(JSONObject)object3.get("summary");
						long likes=(long)object4.get("total_count");
						int likeCount=(int)likes;
						m.addLikes(index, likeCount);
						
						//
						//Comments
						//
						String response3=m.enviarSolicitud(m.crearSolicitudComentarios(id));
						Object obj4=parser.parse(response3);
						JSONObject object5=(JSONObject)obj4;
						JSONObject object6=(JSONObject)object5.get("summary");
						JSONArray arrayComments=(JSONArray)object5.get("data");
						long comments=(long)object6.get("total_count");
						int commCount=(int)comments;
						m.addComments(index, commCount);
						
						//
						//Data
						//
						post.put("msg",message);
						post.put("reaction_count",likeCount);
						post.put("comment_count",commCount);
						post.put("time",fecha);
						post.put("comments", arrayComments);
						
						op.println(post.toJSONString());
					}
					op.println(response);
					int[]lks=m.getLikesPorHora();
					for (int i = 0; i < lks.length; i++) {
						System.out.println(lks[i]);
						//op2.println(lks[i]);
					}
					} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					continue;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					continue;
				}
				}
//		}
		
		int[]lks=m.getLikesPorHora();
		int[]comms=m.getCommentsPorHora();
		for (int i = 0; i < lks.length; i++) {
			System.out.println(lks[i]);
			op2.println(lks[i]);
		}
		for (int i = 0; i < comms.length; i++) {
			op2.println(comms[i]);
		}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
