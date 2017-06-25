package br.uff.redes1.server;

import br.uff.redes1.FileUtil;
import br.uff.redes1.ipv4.Datagram;

import java.io.File;
import java.util.*;

/**
 * Created by rodcastro on 25/06/17.
 */
public class Router {
    // Interfaces (neighbours) that this router can route to.
    private List<Neighbour> interfaces;

    // Routes definitions. The hash key is the datagram destination, the value is the interface it needs to send it to.
    private HashMap<Neighbour, Neighbour> routes;

    private boolean valid;

    public Router(List<Neighbour> interfaces, File routingFile) {
        valid = false;
        loadRoutes(routingFile);
    }

    private void loadRoutes(File routingFile) {
        try {
            List<String[]> lines = FileUtil.readFile(routingFile);
            for (String[] line : lines) {
                Neighbour destinationNeighbour = new Neighbour(line[0], line[1]);
                Neighbour nextJumpNeighbour = interfaces.get(Integer.parseInt(line[2]));
                routes.put(destinationNeighbour, nextJumpNeighbour);
            }
            this.valid = true;
        } catch (ArrayIndexOutOfBoundsException ex) {
            this.valid = false;
        }
    }

    /**
     * Returns a list with all the hosts this router knows the routes to.
     * @return A list containing hosts with known routes.
     */
    public List<Neighbour> getAvailableHosts() {
        return new ArrayList<>(routes.keySet());
    }

    /**
     * Checks the routes loaded from the file against the aailable interfaces.
     * @return True if all is good, false if there's a route entry trying to route to an unavailable interface.
     */
    public boolean areRoutesValid() {
        return valid;
    }

    public Neighbour findNextJump(Datagram datagram) {
        return findNextJump(datagram.getHeader().getDestinationIp());
    }

    /**
     * Looks up routing table to find the next jump for a given packet.
     * @param destinationIp The destination final address
     * @return The next neighbour to send the packet to, or null if the packet has reached its destination OR it can't
     * be routed to its destination.
     */
    public Neighbour findNextJump(String destinationIp) {
        if (!isFinalDestination(destinationIp)) {
            Iterator it = routes.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Neighbour, Neighbour> pair = (Map.Entry)it.next();
                if (destinationIp.equals(pair.getKey().getAddress())) {
                    return pair.getValue();
                }
                it.remove(); // Avoids a ConcurrentModificationException
            }
        }
        return null;
    }

    public boolean isFinalDestination(Datagram datagram) {
        return isFinalDestination(datagram.getHeader().getDestinationIp());
    }

    /**
     * Checks if the datagram reached its destination.
     * @param destinationIp The destination final address
     * @return True if this instance is the datagram's destination, false otherwise
     */
    public boolean isFinalDestination(String destinationIp) {
        for (Neighbour neighbour : interfaces) {
            if (destinationIp == neighbour.getAddress()) {
                return true;
            }
        }
        return false;
    }
}
