import java.util.*;
import java.io.*;
import java.net.*;

public class ThreadedObjectServer
	{  
		public static void main(String[] args )
			{  
                            ArrayList<ThreadedObjectHandler> handlers = new ArrayList<ThreadedObjectHandler>();
			
                            try
				{
                                    ServerSocket s = new ServerSocket(4450);
                                        for (;;)
                                            {
					Socket incoming = s.accept( );
                                        System.out.println("Client connected from:"+incoming.getLocalAddress().getHostName());
					new ThreadedObjectHandler(incoming, handlers).start();
                                            }   
				}
                            catch(Exception e)
				{  
				System.out.println(e);
				} 
			} 
               
                
	}

class ThreadedObjectHandler extends Thread
	{ 
                public static ArrayList<Socket> ConnectionArray=new ArrayList<Socket>();
                public static ArrayList<String> CurrentUsers=new ArrayList<String>();
                
		Object broadcastobject,writeobject,myObject = null;
		private Socket incoming;
		ArrayList<ThreadedObjectHandler> handlers;
		
                ObjectInputStream in;
		ObjectOutputStream out;

		public ThreadedObjectHandler(Socket incoming, ArrayList<ThreadedObjectHandler> handlers)
			{ 
			this.incoming = incoming;
			this.handlers = handlers;
			handlers.add(this);
			}
	
		public synchronized void broadcast(Object obj)
			{
			
			
			
			
			Iterator<ThreadedObjectHandler> it = handlers.iterator();
                                while(it.hasNext())
				{
                                 ThreadedObjectHandler current = it.next();
				try
					{
					current.out.writeObject(obj);
					current.out.reset();
					}
				catch(IOException e)
					{
					System.out.println(e.getMessage());
					}
				}
                        }
   
	public void run()
		{  
			try
				{ 	
                                        
                                        String username;
                                        String s;
                                        in = new ObjectInputStream(incoming.getInputStream());
					out = new ObjectOutputStream(incoming.getOutputStream());
                                        ConnectionArray.add(incoming);
                                        ChatMessage uname = (ChatMessage)in.readObject();
                                       
                                         username = ((String)uname.getName());
                                        CurrentUsers.add(username);
                                        broadcast(username+": "+"joined the chat");
					for(;;)
					{
                                                broadcast("@#!"+CurrentUsers);
						myObject = in.readObject();
                                                    if(myObject instanceof StringMessage)
							{
                                                            StringMessage sm = (StringMessage)myObject;
                                                            s = (String)sm.getMessage();
                                                            broadcastobject=username+": "+s;
							}
                                                    else if((myObject.toString().contains("remove")))
                                                        {
                                                            broadcast(username+": "+"left the chat");  
                                                            CurrentUsers.remove(username);
                                                            broadcastobject=("@#!"+CurrentUsers);
                                                        }
                                                    else if(myObject instanceof LineMessage)
                                                        {
                                                            ArrayList<Line> message;
                                                            LineMessage lm = (LineMessage)myObject;
                                                            message=(ArrayList)lm.getLineMessage();
                                                            broadcastobject=message;
                                                            broadcast(username+": "+"wrote on the whitebaord");
                                                        }
                                                
                                                broadcast(broadcastobject);
					}		    
				}
			catch(Exception e)
				{  
					System.out.println(e);
				}
			finally
				{
					handlers.remove(this);
					try
						{
						in.close();
						out.close();
						incoming.close();
						}
					catch(IOException e)
						{
						System.out.println(e.getMessage());
						}
				}
		}
        
	}

