import java.io.*;
import java.util.List;

public class CustomSupport {
    public static void printList(List<List<DataCell>> list){
        System.out.println(" ");
        boolean oneTime=true;
        for(List<DataCell> subList: list){
            for(DataCell c: subList){
                String output = c.getData().toString();
                output+="->"+c.getDataType();

                if(oneTime){
                    output+="("+c.headerType+")";
                }

                while (output.length()<Main.SPACE_GAP){
                    output+=" ";
                }
                System.out.print(output);
            }
            oneTime=false;
            System.out.println("");
        }
    }

    public static void searchFor(String column, String data){
        List<DataCell> header = Main.dataList.get(0);
        int counter, i;
        counter=0;
        i=0;
        for(DataCell a: header){
            if(a.data.toString().equals(column)) {System.out.println("Колонка с именем \""+column+"\" была найдена под индексом "+(i)+" - считая от 0"); counter=i;}
            i++;
        }
        i=0;
        for(List<DataCell> list: Main.dataList){
            DataCell b=list.get(counter);
            if(b.data.toString().equals(data)) {System.out.println("Данные с \""+data+"\" были найдены под индексом "+i);
                saveToCSV(Main.dataList.get(i),"cake.csv");
            }
            i++;
        }
    }

    public static void saveToCSV(List<DataCell> list, String filepath){
        if(Main.oneTimeRefreshingFile){
            try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filepath), //Здесь просто создаю или обновляю файл если уже создан
                    Main.ENCODING))) { writer.write(""); } catch (Exception e) { e.printStackTrace(); }
            Main.oneTimeRefreshingFile=false;
        }

        try{
            PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(filepath, true)));
            StringBuilder sb = new StringBuilder("");
            for(DataCell cell:list){ sb.append(cell.data.toString()+Main.DELIMITER); }
            sb.deleteCharAt(sb.lastIndexOf(";"));
            printWriter.print(sb.toString());
            printWriter.println();
            printWriter.flush();
            printWriter.close();
        }catch(Exception e){

        }
    }
}
