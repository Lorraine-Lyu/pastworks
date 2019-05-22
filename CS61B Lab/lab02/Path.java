/** A class that represents a path via pursuit curves. */
public class Path {
  public Point next = new Point();
  public Point curr = new Point();

    public Path(double x, double y){
      // System.out.println("DAF");
       this.next = new Point(x, y);
        this.curr = new Point();

    }

    public void iterate(double dx, double dy){
        this.curr = this.next;
        this.next = new Point(this.curr.x + dx, this.curr.y + dy);
    }

}
