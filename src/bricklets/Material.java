package bricklets;

/**
 * Created with IntelliJ IDEA.
 * User: davidrusu
 * Date: 27/11/12
 * Time: 11:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class Material {
    private static MaterialData[][] table;
    private static double growRate = 1.5;
    private static int width = 1, numberOfMaterials = 0;
    private static Material rubber, steel, ice;

    private int materialNumber;

    static {
        table = new MaterialData[10][1];
        for(int i = 0; i < table.length; i++){
            table[i][0] = new MaterialData(1, 1);
        }
        rubber = new Material(getNextMaterialNumber());
        steel = new Material(getNextMaterialNumber());
        ice = new Material(getNextMaterialNumber());
        setMaterialData(rubber.materialNumber, rubber.materialNumber, 1.16, 1);
        setMaterialData(rubber.materialNumber, steel.materialNumber, 0.5, 1);
        setMaterialData(rubber.materialNumber, ice.materialNumber, 0.15, 1);
        setMaterialData(steel.materialNumber, steel.materialNumber, 0.8, 1);
        setMaterialData(steel.materialNumber, ice.materialNumber, 0.1, 1);
        setMaterialData(ice.materialNumber, ice.materialNumber, 0.05, 1);
    }

    private Material(int materialNumber) {
        this.materialNumber = materialNumber;

    }

    public static Material getRubber() {
        return rubber;
    }

    public static Material getSteel(){
        return steel;
    }

    public static Material getIce(){
        return  ice;
    }

    public static Material createCustomMaterial(double friction, double restitution){
        int materialNumber = getNextMaterialNumber();
        setMaterialData(materialNumber, 0, friction, restitution);
        return new Material(materialNumber);
    }

    public static double getFriction(Material a, Material b){
        int x = Math.max(a.materialNumber, b.materialNumber);
        int y = Math.min(a.materialNumber, b.materialNumber);

        // is there an entry for these two materials in the table?
        if(y < table[x].length){
            return table[x][y].getFriction();
        }
        // the values are stored square rooted so the expanded equation here is:
        // friction = sqrt(frictionA * frictionB)
        return table[x][0].getFriction() * table[y][0].getFriction();
    }

    public static double getRestitution(Material a, Material b){
        int x = Math.max(a.materialNumber, b.materialNumber);
        int y = Math.min(a.materialNumber, b.materialNumber);

        // is there no entry for these two materials in the table?
        if(y >= table[x].length){
            // the values are stored square rooted so the expanded equation here is:
            // friction = sqrt(restitutionA * restitutionB)
            return table[x][0].getRestitution() * table[y][0].getRestitution();
        }
        return table[x][y].getRestitution();
    }

    private static void expandX(){
        MaterialData[][] temp = table;
        table = new MaterialData[(int) (width * growRate + 1)][];
        for (int i = 0; i < table.length; i++) {
            if(i < width){
                MaterialData[] column = temp[i];
                table[i] = new MaterialData[column.length];
                System.arraycopy(column, 0, table[i], 0, column.length);
            }else{
                table[i] = new MaterialData[1];
            }
        }
    }

    private static void expandColumn(int column, int newLength) {
        MaterialData[] temp = table[column];
        table[column] = new MaterialData[newLength];
        System.arraycopy(temp, 0, table[column], 0, temp.length);
    }

    private static int getNextMaterialNumber(){
        numberOfMaterials++;
        return numberOfMaterials;
    }

    private static void setMaterialData(int materialNumberA, int materialNumberB, double friction, double restitution) {
        int x = Math.max(materialNumberA, materialNumberB);
        int y = Math.min(materialNumberA, materialNumberB);

        // need a loop because we can't trust that one expandX will in large the table to be larger than x
        while (x >= table.length) {
            expandX();
        }

        if(x >= width){
            width = x + 1;
        }

        if (y == 0) {
            table[x][y] = new MaterialData(Math.sqrt(friction), Math.sqrt(restitution));
        } else {
            if(y >= table[x].length){
                expandColumn(x, y + 1);
            }
            table[x][y] = new MaterialData(friction, restitution);
        }
    }
}
