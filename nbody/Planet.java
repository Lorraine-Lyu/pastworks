public class Planet{
    double xxPos, yyPos, xxVel, yyVel, mass;
    String imgFileName;
    private double G = 6.67 * Math.pow(10, -11);
//    Planet[] allPlanets = {samh, rocinante, aegir};


    public Planet(double xP, double yP, double xV, double yV, double m, String img){
        xxPos = xP;
        yyPos = yP;
        xxVel = xV;
        yyVel = yV;
        mass = m;
        imgFileName = img;
    }

    public Planet(Planet p){
        xxPos = p.xxPos;
        yyPos = p.yyPos;
        xxVel = p.xxVel;
        yyVel = p.yyVel;
        mass = p.mass;
        imgFileName = p.imgFileName;
    }

    public double calcDistance(Planet p){
        double xxDis = p.xxPos - this.xxPos;
        double yyDis = p.yyPos - this.yyPos;
        double distance = Math.sqrt(xxDis * xxDis + yyDis * yyDis);
        return distance;
    }

    public double calcForceExertedBy(Planet p){
        double Force = p.mass * this.mass * G/ Math.pow(this.calcDistance(p), 2);
        return Force;
    }

    public double calcForceExertedByX(Planet p){
        double Force = this.calcForceExertedBy(p);
        double Distance = this.calcDistance(p);
        double ForceExertedByX = Force * (p.xxPos - this.xxPos) / Distance;
        return ForceExertedByX;
    }

    public double calcForceExertedByY(Planet p){
        double Force = this.calcForceExertedBy(p);
        double Distance = this.calcDistance(p);
        double ForceExertedByY = Force * (p.yyPos - this.yyPos) / Distance;
        return ForceExertedByY;
    }

    public double calcNetForceExertedByX(Planet[] allPlanets){
        double NetForceExertedByX = 0;
        for (int i = 0; i < allPlanets.length; i++){
            if (allPlanets[i].equals(this)){
                continue;
            }
            NetForceExertedByX += calcForceExertedByX(allPlanets[i]);
        }
        return NetForceExertedByX;
    }

    public double calcNetForceExertedByY(Planet[] allPlanets){
        double NetForceExertedByY = 0;
        for (int i = 0; i < allPlanets.length; i++){
            if (allPlanets[i].equals(this)){
                continue;
            }
            NetForceExertedByY += calcForceExertedByY(allPlanets[i]);
        }
        return NetForceExertedByY;
    }

    public void update(double dt, double fX, double fY) {
        double aX = fX / this.mass;
        double aY = fY / this.mass;
        xxVel += dt * aX;
        yyVel += dt * aY;
        xxPos += xxVel * dt;
        yyPos += yyVel * dt;
    }

    public void draw() {
        StdDraw.picture(xxPos, yyPos, imgFileName);
    }
//    public static void main(String[] args){
//
//    }
}