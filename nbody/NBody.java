public class NBody {
    public static double readRadius(String planetsTxtPath){
        In in = new In(planetsTxtPath);
        int number = in.readInt();
         double radius = in.readDouble();
        return radius;
    }

    public static Planet[] readPlanets(String p){
        In in = new In(p);
        int number = in.readInt();
        double radius = in.readDouble();
        Planet[] allPlanets;
        allPlanets = new Planet[number];
        for (int i = 0; i < number; i ++){

                double xxPos = in.readDouble();
                double yyPos = in.readDouble();
                double xxVel = in.readDouble();
                double yyVel = in.readDouble();
                double mass = in.readDouble();
                String imgFileName = in.readString();
                allPlanets[i] = new Planet(xxPos, yyPos, xxVel, yyVel, mass, imgFileName);

        }
        return allPlanets;
    }




    public static void main(String[] args) {
        double T = Double.parseDouble(args[0]);
        double dt =  Double.parseDouble(args[1]);
        String filename = args[2];
        Double radius = readRadius(filename);
        Planet[] allPlanets= readPlanets(filename);
//        int size = (int)radius;
        StdDraw.setScale(-radius, radius);
//        StdDraw.picture(0, 0, "images/starfield.jpg");
//        for (Planet p : allPlanets){
//            p.draw();
//        }
        StdDraw.enableDoubleBuffering();
        for (int time = 0; time < T; time += dt){
            Double[] xForces = new Double[allPlanets.length];
            Double[] yForces = new Double[allPlanets.length];
            for (int i = 0; i < allPlanets.length; i++) {
                xForces[i] = allPlanets[i].calcNetForceExertedByX(allPlanets);
                yForces[i] = allPlanets[i].calcNetForceExertedByY(allPlanets);
                allPlanets[i].update(dt, xForces[i], yForces[i]);
            }
            StdDraw.picture(0, 0, "images/starfield.jpg");
            for (Planet p : allPlanets){
                p.draw();
            }
            StdDraw.show();
            StdDraw.pause(10);

        }
        StdOut.printf("%d\n", allPlanets.length);
        StdOut.printf("%.2e\n", radius);
        for (int i = 0; i < allPlanets.length; i += 1) {
            StdOut.printf("%11.4e %11.4e %11.4e %11.4e %11.4e %12s\n",
                    allPlanets[i].xxPos, allPlanets[i].yyPos, allPlanets[i].xxVel,
                    allPlanets[i].yyVel, allPlanets[i].mass, allPlanets[i].imgFileName);
        }
    }
}
