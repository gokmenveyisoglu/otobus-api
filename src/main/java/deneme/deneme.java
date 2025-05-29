package deneme;

public class deneme {
    public static  void  main(String[] args) throws Exception {

try(Ableable islemyap=new Ableable()) { //AutoCloseable
    islemyap.selam();
}
        Ableable islemyap=new Ableable();
        islemyap.selam();
        islemyap4 mahmut=new islemyap4();

        String a="A";
        int j = 0;
        try { // hata yakalama

             j=Integer.parseInt(a);
        }catch (Exception e){
            j=Integer.parseInt("5");
        }finally {
            System.out.println(j);
        }



    }
}
