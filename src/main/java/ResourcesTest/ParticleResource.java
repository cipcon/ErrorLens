package ResourcesTest;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Set;

import Test.Particle;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/particles")
public class ParticleResource {

    @GET
    public Set<Particle> getUsers() {
        Set<Particle> particleList = Collections.newSetFromMap(Collections.synchronizedMap(new LinkedHashMap<>()));

        Particle particle = new Particle();
        particle.setName("Gravitation");
        particleList.add(particle);

        Particle particle2 = new Particle();
        particle2.setName("Pentaquark");
        particleList.add(particle2);

        return particleList;
    }

}