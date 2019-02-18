package main;


public abstract class Client
{

    
    private Connection hatVerbindung;
    private Clientempfaenger hatEmpfaenger;
     
  
    class Clientempfaenger extends Thread
    {
       
        private Client kenntClient;
        private Connection kenntVerbindung;
        
        
        private boolean zVerbindungAktiv;
    
    
        public Clientempfaenger(Client pClient, Connection pConnection)
        {
            kenntClient = pClient;
            kenntVerbindung = pConnection;
            zVerbindungAktiv = true;
        }
        
    
        public void run()
        {
            String lNachricht;
            boolean lNachrichtEmpfangen = true;
            
            do
                if (zVerbindungAktiv)
                {
                    lNachricht = kenntVerbindung.receive();
                    lNachrichtEmpfangen = (lNachricht != null);
                    if (lNachrichtEmpfangen)
                        kenntClient.processMessage(lNachricht); 
                }
            while (zVerbindungAktiv && lNachrichtEmpfangen);
        }
        
    
        public void gibFrei()
        {
            zVerbindungAktiv = false;
        }
        
    }
    
  
    public Client(String pIPAdresse, int pPortNr)
    {
        hatVerbindung = new Connection(pIPAdresse, pPortNr); 
        
        try
        {
            hatEmpfaenger = new Clientempfaenger(this, hatVerbindung);
            hatEmpfaenger.start();
        }

        catch (Exception pFehler)
        {
            System.err.println("Fehler beim \u00D6ffnen des Clients: " + pFehler);
        }       
         
    }
    
    public void send(String pMessage)
    {
        hatVerbindung.send(pMessage);
    }

    public boolean istVerbunden()
    {  if (hatEmpfaenger != null)
         return hatEmpfaenger.zVerbindungAktiv;
       else
         return false;
    }
    public String toString()
    {
        return "Verbindung mit Socket: " + hatVerbindung.verbindungsSocket();
    }
    
  
    public abstract void processMessage(String pMessage);

  
    public void close()
    {
        if (hatEmpfaenger != null)
            hatEmpfaenger.gibFrei();
        hatEmpfaenger = null;
        hatVerbindung.close();
    }

}
