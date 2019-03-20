import java.io.*;
import java.util.List;

/**
 * Класс CustomSupport создан для вынесения вспомогательных функций
 */
public class CustomSupport {

    /**
     * Выводим в консоль структурированные данные.
     *
     * @param list подаём список на вывод
     */
    public static void printList(List<List<DataCell>> list){
        if(list.size()<Main.OUTPUT_ROWS&list.get(0).size()<Main.OUTPUT_COLUMNS){
            subModule(list);
        }else{
            System.out.println("Слишком большие данные для вывода в консоль.");
        }
    }

    /** Вспомогательный можуль для уменьшения кода*/
    private static void subModule(List<List<DataCell>> list) {
        System.out.println(" ");
        /**
         * @param oneTime нужен для единоразового вывода подтипа каждой ячейки(как раз для типа Header)
         */
        boolean oneTime=true;
        for(List<DataCell> subList: list){
            for(DataCell c: subList){
                String output = c.getData().toString();
                output+="->"+c.getDataType();

                if(oneTime){
                    output+="("+c.headerType+")";
                }

                /**  Цикл используется для построения красивого списка при выводе в консоль  */
                while (output.length()<Main.SPACE_GAP){
                    output+=" ";
                }
                System.out.print(output);
            }
            oneTime=false;
            System.out.println("");
        }
    }

    /**
     * Метод для поиска данных.
     * Настраиваемый - можно вывести всю информацию с необходимыи
     * значением, или только первый найденный результат.
     * @param column Стринговое значение с названием необходимой колонки
     * @param data   Стринговое значение с данными для поиска.
     */
    public static void searchFor(String column, String data){
        boolean oneTimeColumn=true;
        boolean found=false;
        List<DataCell> header = Main.dataList.get(0);
        int counter, i;
        counter=0;
        i=0;
        /** Находит колонку с нужным названием только 1 раз. Если нужно несколько -
         *  необходимо создать массив и в нём хранить значения.  */
        for(DataCell a: header){
            if(a.data.toString().equals(column)&&oneTimeColumn) {
                System.out.println("Колонка с именем \""+column+"\" была найдена под индексом "+(i)+" - считая от 0");
                checkRequestForAccuracy(a.headerType, data);
                counter=i;  oneTimeColumn=false;}
            i++;
        }
        i=0;
        for(List<DataCell> list: Main.dataList){
            /** По индексу нужного столбика перебираем данные между списками */
            DataCell b=list.get(counter);
            if(b.data.toString().equals(data)) {System.out.println("Данные с \""+data+"\" были найдены под индексом "+i);
            found=true;
                /** saveToCSV записывает данные построчно - по 1 строчке в csv файл */
                saveToCSV(Main.dataList.get(i));
                /** Проверка на флаг - разрешение поиск и запись нескольких результатов. Если
                 * разрешения нет - после нахождения 1 результата поиск и запись прерывается.*/
                if(!Main.multimpleResults){
                    break;
                }
            }
            i++;
        }
        if(!found) System.out.println("Данные \""+data+"\" не были найдены. Проверьте подаваемые значения.");
    }

    /** запись построчно - по 1 строчке в csv файл.
     * В самом начале создаётся или обновляется csv файл с нужным названием.
     * */
    public static void saveToCSV(List<DataCell> list){
        if(Main.oneTimeRefreshingFile){
            try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Main.OutputName), //Здесь просто создаю или обновляю файл если уже создан
                    Main.ENCODING))) { writer.write(""); } catch (Exception e) { e.printStackTrace(); }
            Main.oneTimeRefreshingFile=false;
        }

        /**
         * Затем записывается нужная строчка.
         * В случае, если нужно записать несколько строчек - метод вызывается несколько раз.
         */
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

    /**
     *
     * Простая проверка на правильность запроса.
     * Типичные ошибки, которые может обнаружить:
     * попытка найти "AVD" в колонке с типом Integer || Float || Date
     * или попытка найти "1231" в колонке с типом Date
     */
    public static void checkRequestForAccuracy(Main.DataType columnDatatype,String data){
        boolean correct = true;
        boolean b_string=false;
        Object a = new Object();
        switch (columnDatatype){
            case STRING:
                /**Здесь без проверок так как тип переменной изначально String. */
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
            System.out.println("Ошибка в поисковом запросе. Поиск в колонке с типом "+columnDatatype+" заданное значение имеет другой тип");
        }else if(b_string){
            System.out.println("Ошибка в поисковом запросе. Поиск в колонке с типом "+columnDatatype+", заданное значение имеет тип String");
        }else{
            System.out.println("Какая-то несостыковка в типах данных.");
        }
    }
}
