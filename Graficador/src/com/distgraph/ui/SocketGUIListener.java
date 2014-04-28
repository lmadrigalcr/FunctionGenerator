package com.distgraph.ui;

import com.distgraph.net.ClientConnectedEvent;
import com.distgraph.net.DataRecievedEvent;
import com.distgraph.net.SocketListener;
import com.distgraph.util.Utilities;

/**
 * La clase SocketGUIListener actualiza la interfaz gráfica con los datos que lleguen del Socket.
 */
public class SocketGUIListener implements SocketListener 
{
    private final DistributionGraph frame;
    private int dataRecievedCount;
    private int clientConnectedCount;
    
    /**
     * Crea un nuevo objeto SocketGUIListener.
     * 
     * @param frame El objeto DistributionGraph que se desea actualizar.
     */
    public SocketGUIListener(DistributionGraph frame)
    {
        this.frame = frame;
        this.dataRecievedCount = 0;
        this.clientConnectedCount = 0;
    }
    
    @Override
    public void handleDataRecieved(DataRecievedEvent e) 
    {
        switch (this.dataRecievedCount++)
        {
            // Primer Dato: Nombre de la función.
            case 0:
                String distributionName = this.clientConnectedCount + ". " + e.getData().toUpperCase();
                this.frame.renameSerie(distributionName);
                break;
            // Segundo Dato: Rango en formato ini:fin:intervalo
            case 1:
                try
                {
                    double[] range = Utilities.splitAndCast(e.getData(), ":");
                    this.frame.setRange(range[0], range[1], range[2]);
                }
                catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) 
                {
                    System.out.println(ex.getMessage());
                    System.out.println("Error: Invalid Range.");
                    System.out.println(e.getData());
                }
                break;
            // Siguientes Datos: Números.
            default:
                try
                {
                    double x = Double.valueOf(e.getData());
                    this.frame.incrementValue(x);
                }
                catch (NullPointerException ex)
                { 
                    System.out.println("Error: Received NULL data.");
                }
                catch (NumberFormatException ex) { }
        }
    }

    @Override
    public void handleClientConnected(ClientConnectedEvent e)
    {
        this.clientConnectedCount++;
        this.dataRecievedCount = 0;
        this.frame.createNewSerie();
    }
}