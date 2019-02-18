package main;


import java.net.*;


public class Server
{
  
  
  private ServerSocket serverSocket;
  private List verbindungen;
  private ServerSchleife schleife;
  
  
  private int zPort;
  
  private class ServerConnection extends Connection
  {
    
    Server server;
    
    public ServerConnection(Socket pSocket, Server pServer)
    {
      super(pSocket);
      server = pServer;
    }
    
    
    public void run()
    {
      String lNachricht;
      
      while (!this.isClosed())
      {
        lNachricht = this.receive();
        if (lNachricht == null)
        {
          if (!this.isClosed())
          {
            server.closeConnection(this.getRemoteIP(), this.getRemotePort());
          }
        }
        else
        server.processMessage(this.getRemoteIP(), this.getRemotePort(), lNachricht);
      }
    }
    
  }   
  
  private class ServerSchleife extends Thread
  {
    
    private Server server;
    
    public ServerSchleife(Server pServer)
    {
      server = pServer;
    }
    
    public void run()
    {
      while (true)
      {
        try
        {
          Socket lClientSocket = server.serverSocket.accept();
          ServerConnection lNeueSerververbindung = new ServerConnection(lClientSocket, server);
          server.ergaenzeVerbindung(lNeueSerververbindung);
          lNeueSerververbindung.start();
        }
        
        catch (Exception pFehler)
        {
          System.err.println("Fehler beim Erwarten einer Verbindung in Server: " + pFehler);
        }    
      }
    }               
  }
  
  public Server(int pPortNr)
  {
    try
    {
      serverSocket = new ServerSocket(pPortNr);
      zPort = pPortNr;
      verbindungen = new List();
      schleife = new ServerSchleife(this);
      schleife.start();
    }
    
    catch (Exception pFehler)
    {
      System.err.println("Fehler beim \u00D6ffnen der Server: " + pFehler);
    }       
  }
  
  public String toString()
  {
    return "Server von ServerSocket: " + serverSocket;
  }
  
  private void ergaenzeVerbindung(ServerConnection pVerbindung)
  {
    verbindungen.append(pVerbindung);
    this.processNewConnection(pVerbindung.getRemoteIP(), pVerbindung.getRemotePort());
  }
  
  
  private ServerConnection SerververbindungVonIPUndPort(String pClientIP, int pClientPort)
  {
    ServerConnection lSerververbindung;
    
    verbindungen.toFirst();
    
    while (verbindungen.hasAccess())
    {
      lSerververbindung = (ServerConnection) verbindungen.getObject();
      if (lSerververbindung.getRemoteIP().equals(pClientIP) && lSerververbindung.getRemotePort() == pClientPort)
      return lSerververbindung;
      verbindungen.next();
    }   
    
    return null;
  }
  
  
  public void send(String pClientIP, int pClientPort, String pMessage)
  {
    ServerConnection lSerververbindung = this.SerververbindungVonIPUndPort(pClientIP, pClientPort);
    if (lSerververbindung != null)
    lSerververbindung.send(pMessage);
    else
    System.err.println("Fehler beim Senden: IP " + pClientIP + " mit Port " + pClientPort + " nicht vorhanden.");
  }
  
  
  public void sendToAll(String pMessage)
  {
    ServerConnection lSerververbindung;
    verbindungen.toFirst();
    while (verbindungen.hasAccess())
    {
      lSerververbindung = (ServerConnection) verbindungen.getObject();
      lSerververbindung.send(pMessage);
      verbindungen.next();
    }   
  }
  
  
  public void closeConnection (String pClientIP, int pClientPort)
  {
    ServerConnection lSerververbindung = this.SerververbindungVonIPUndPort(pClientIP, pClientPort);
    if (lSerververbindung != null)
    {   this.processClosedConnection(pClientIP, pClientPort);
      lSerververbindung.close();
      this.loescheVerbindung(lSerververbindung);
      
    }
    else
    System.err.println("Fehler beim Schlie\u00DFen der Verbindung: IP " + pClientIP + " mit Port " + pClientPort + " nicht vorhanden.");
    
  }
  
  
  private void loescheVerbindung(ServerConnection pVerbindung)
  {
    verbindungen.toFirst();
    while (verbindungen.hasAccess())
    {
      ServerConnection lClient = (ServerConnection) verbindungen.getObject();
      if (lClient == pVerbindung)
      verbindungen.remove();
      verbindungen.next();
    }   
  }
  
  
  public void processNewConnection(String pClientIP, int pClientPort){
    
  }
  
  
  
  public void processMessage(String pClientIP, int pClientPort, String pMessage){
  }
  
  
  public void processClosedConnection(String pClientIP, int pClientPort)
  {}
  
  public void close()
  {
    try
    {
      serverSocket.close(); serverSocket = null;
    }
    
    catch (Exception pFehler)
    {
      System.err.println("Fehler beim Schlie\u00DFen des Servers: " + pFehler);
    }
    
  }
  
}