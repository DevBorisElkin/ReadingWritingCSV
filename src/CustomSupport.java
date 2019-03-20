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
        boolean oneTimeColumn=true;
        List<DataCell> header = Main.dataList.get(0);
        int counter, i;
        counter=0;
        i=0;
        for(DataCell a: header){
            if(a.data.toString().equals(column)&&oneTimeColumn) {
                System.out.println("Колонка с именем \""+column+"\" была найдена под индексом "+(i)+" - считая от 0");
                checkRequestForAccuracy(a.headerType, data);
                counter=i;  oneTimeColumn=false;}
            i++;
        }
        i=0;
        for(List<DataCell> list: Main.dataList){
            DataCell b=list.get(counter);
            if(b.data.toString().equals(data)) {System.out.println("Данные с \""+data+"\" были найдены под индексом "+i);
                saveToCSV(Main.dataList.get(i));
                if(!Main.multimpleResults){
                    break;
                }
            }
            i++;
        }
    }

    public static void saveToCSV(List<DataCell> list){
        if(Main.oneTimeRefreshingFile){
            try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Main.OutputName), //Здесь просто создаю или обновляю файл если уже создан
                    Main.ENCODING))) { writer.write(""); } catch (Exception e) { e.printStackTrace(); }
            Main.oneTimeRefreshingFile=false;
        }

        try{
            PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(Main.OutputName, true)));
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

    public static void checkRequestForAccuracy(Main.DataType columnDatatype,String data){
        //STRING, DATE, INTEGER, FLOAT, HEADING, WRONG_TYPE, NONE
        boolean correct = true;
        boolean b_string=false;
        Object a = new Object();
        switch (columnDatatype){
            case STRING:
                //Здесь без проверок так как тип переменной изначально String.
                b_string=true;
                break;
            case FLOAT:
                try{a = Float.parseFloat(data);
                } catch (NumberFormatException e){ correct=false; }
                break;
            case INTEGER:
                try{a = Integer.parseInt(data);
                } catch (NumberFormatException e){ correct=false; }
                break;
            case DATE:
                if(SimpleDate.checkIfDateIsWrong(data)){ correct=false;
                }else{ a=new SimpleDate(data);}
                break;
        }
        if(!correct&!b_string) {
            System.out.println("Ошибка в поисковом запросе. Поиск в колонке с типом "+columnDatatype+" значение имеет другой тип");
        }else if(b_string){
            System.out.println("Ошибка в поисковом запросе. Поиск в колонке с типом "+columnDatatype+", значение имеет тип String");
        }else{
            System.out.println("Неизвестная ошибка в типах");
        }
    }
}
