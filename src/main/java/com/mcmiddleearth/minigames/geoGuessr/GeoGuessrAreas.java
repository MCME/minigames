package com.mcmiddleearth.minigames.geoGuessr;

public enum GeoGuessrAreas {
    All("all","a",-12000,-12000,17000,15000),
    Eriador("Eriador","b",-7000,-8500,3400,-800),
    ROHAN("Rohan","c",700,-600,4200,2500),
    ANORIEN_and_Ithilien("Anorien and Ithilien","d",4600,500,9700,3700),
    Gondor("Gondor","e",-5500,2400,9700,8000),
    Shire("the Shire","f",-5600,-5400,-3000,-3200),
    Anfalas_Lefnui_Pinath("Anfalas and Pinnath Gelin","g",-2400,2700,2150,6000),
    Lebennin("Lebennin","h",5300,4200,7800,6600),
    Belfalas_and_Lamedon("Belfalas and Lamedon","i",2160,2520,5200,8200),
    Misty_Mountains("the Misty Mountains","j",2030,700,5500,-4000),
    TEST("Test","t",10,10,20,20);

    private final String name;
    private final String abb;
    private final double x1;
    private final double z1;
    private final double x2;
    private final double z2;
    
    GeoGuessrAreas(String name, String abb,double x1,double z1, double x2, double z2){
        this.name = name;
        this.abb = abb;
        this.x1 = x1;
        this.z1 = z1;
        this.x2 = x2;
        this.z2 = z2;
    }
    public String getName(){return name;}
    public double x1(){return x1;}
    public double z1(){return z1;}
    public double x2(){return x2;}
    public double z2(){return z2;}

    public static GeoGuessrAreas getArea(String name){
        for(GeoGuessrAreas area: GeoGuessrAreas.values()){
            if(area.abb.equalsIgnoreCase(name)){
                return area;
            }
        }
        return null;
    }
}
