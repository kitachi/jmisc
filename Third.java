package testproj;

import java.io.FileNotFoundException;

public class Third extends Another{
//    public class MyClass {
//        private interface MyInterface extends Runnable, WindowListener { 
//        }
//
//        Runnable r = new MyInterface() {
//         // your anonymous class which implements 2 interaces
//        }
//
//      }
    
    protected class DO extends Object {

        public DO(String pi) throws FileNotFoundException {
            throw new FileNotFoundException("no file is found for pi " + pi);
        }
        
        // public class AccessCopy extents Copy
        //     public AccessCopy(final String workPid) throws FileNotFoundException
        
        // public Copy accessCopy(final String pi) {
        //       return new AccessCopy(workPid);
        // }
        
    }
    
    
    public Object accessDo(String pi) {
        return new Object();
    }
    
    public void validate(String pi) throws FileNotFoundException {
         new DO(pi);
    }
    
    public static Another another() {
        return new Third() {
            
            public Object accessDo(String pi) {
                return new Object();
            }
            
            // @Override
            public void validate(String pi) throws FileNotFoundException {
                new DO(pi);
            }
        };
    }
    
    public static void main(String[] args) {
        try {
            new Third().validate(args[0]);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
