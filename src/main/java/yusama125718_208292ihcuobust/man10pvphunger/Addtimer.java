package yusama125718_208292ihcuobust.man10pvphunger;

import static java.lang.Thread.sleep;

public class Addtimer extends Thread
{
    public void start()
    {
        try
        {
            sleep(30000);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}
