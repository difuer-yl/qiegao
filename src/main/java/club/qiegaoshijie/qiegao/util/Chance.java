package club.qiegaoshijie.qiegao.util;

public class Chance
{
    private boolean success = false;

    public Chance(double num)
    {
        int i = (int)(Math.random() * 100.0D) + 1;
        if (i <= (int)(num * 100.0D)) {
            this.success = true;
        }
    }

    public boolean success()
    {
        return this.success;
    }
}
