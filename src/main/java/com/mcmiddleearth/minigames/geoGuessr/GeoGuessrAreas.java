package com.mcmiddleearth.minigames.geoGuessr;

public enum GeoGuessrAreas {
    All("a",-12000,-12000,17000,15000),
    Eriador("b",-7000,-8500,3400,-800),
    ROHAN("c",700,-600,4200,2500),
    ANORIEN_and_Ithilien("d",4600,500,9700,3700),
    Gondor("e",-5500,2400,9700,8000),
    Shire("f",-5600,-5400,-3000,-3200),
    Anfalas_Lefnui_Pinath("g",-2400,2700,2150,6000),
    Lebennin("h",5300,4200,7800,6600),
    Belfalas_and_Lamedon("i",2160,2520,5200,8200),
    Misty_Mountains("j",2030,700,5500,-4000);

    private final String name;
    private final double x1;
    private final double z1;
    private final double x2;
    private final double z2;
    
    GeoGuessrAreas(String name, double x1,double z1, double x2, double z2){
        this.name = name;
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
            if(area.name.equalsIgnoreCase(name)){
                return area;
            }
        }
        return null;
    }
}
