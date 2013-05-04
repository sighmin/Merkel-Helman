
package merkel.hellman;

/**
 *
 * @author simon
 */
public class U {
    static public void p(String s){
        System.out.println(s);
    }
    
    static public void p(int s){
        System.out.println(s);
    }
    
    static public void p(char[] s){
        System.out.println(s);
    }
    
    static public void p(int[] s){
        for (int i : s)
            System.out.print(i + ",");
        System.out.println("");
    }
    
    static public void p(byte[] data){
        for (byte b : data){
            System.out.print(Integer.toBinaryString(b));
        }
    }
    
    static public char[] toCharArr(byte[] data){
        char[] chars;
        
        String temp = "";
        for (byte b : data){
            temp += Integer.toBinaryString(b);
        }
        
        int difference = 8 - temp.length();
        
        for (int i = 0; i < difference; ++i){
            temp = "0" + temp;
        }
        
        chars = temp.toCharArray();
        
        return chars;
    }
}
