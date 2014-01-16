package archaic;

public class ExceptionTest 
{
    public static void main (String [ ] args)
    {
        try
        {
            /* Hier wandeln wir die Übergabeparameter in Integer-Werte um */
            //int dividend = Integer.parseInt(args[0]);
            //int divisor = Integer.parseInt(args[1]);
 
            /* Erzeugung eines Objektes unserer Exception */
            double ergebnis =  divide(1,0);
 
            /* Anweisungen nach einer throw-Anweisung in einem Block werden nicht mehr ausgeführt */
            System.out.println("Das Ergebnis der Division lautet: "+ergebnis);
        }
         /* Hier fangen wir unsere geworfene Exception auf */
        catch(DivisionByZeroException e)
        {
             /* Hier geben wir NUR unsere Fehlermeldung aus */
            System.out.println(e.getMessage());
 
            /* Hier geben wir die Aufruferliste aus */
            e.printStackTrace();
        }
    }
 
    public static double divide (int dividend, int divisor) throws DivisionByZeroException
    {
        double ergebnis=0;
 
        if(divisor != 0)
        {
            ergebnis = dividend/divisor;
        }
        else
        {
            /* Hier wird unsere Exception geworfen */
            throw new DivisionByZeroException();
 
            /* Anweisungen nach einer throw Anweisung in einem Block werden nicht mehr ausgeführt */
        }
        return ergebnis;
    }
}
 
class DivisionByZeroException extends Exception
{
    DivisionByZeroException()
    {
        super("Der Divisor beträgt 0!");
    }
}