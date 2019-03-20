import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/* Справка по args:
   пример вводимой в консоль строки: java –jar search_csv.jar –in in_file.csv –out out_file.csv -enc UTF-8 -col Дата_рождения -exp 18.06.1983
                                                                0     1        2        3        4    5      6       7          8      9
*/
public class Main {
    public static final String DELIMITER = ";";
    public static final int SPACE_GAP=30;

    //Стандарные значения, меняются на введённые
    public static String InputName = "input.csv";       //
    public static String OutputName = "output.csv";     // Значения заданы, но мы их меняем вводя свои знач. из консоли.
    public static String ENCODING = "utf-8";            //
    public static String ColumnToSearch;
    public static String DataToSearch;

    public static boolean simplifiedSearchRequest = true;   // Если True, упрощённых поисковый запрос. Если false - поиск.запрос как в примере из задания.
    public static boolean multimpleResults = false;         // Если True, выведет ВСЕ значения в нужной колонке по поисковому запросу, если False - только первое.
    public static boolean printListToConsole = true;

    public static boolean oneTimeRefreshingFile=true;   // Используется для создания или пересоздания файла вывода
    public static int WrongDataErrorCounter = 0;        // Счётчик для ошибок в данных

    public static List<List<DataCell>> dataList = new ArrayList<>();
    public enum DataType { STRING, DATE, INTEGER, FLOAT, HEADING, WRONG_TYPE, NONE}
    public static List<DataType> dataTypeList = new ArrayList<>();


    public static void main(String[] args) throws FileNotFoundException{
        action(args);
    }

    private static void action(String[]args) throws FileNotFoundException {


        if(args.length>0){
            //Здесь проверяем, усложнённый ли поисковый запрос, или упрощённый. В урощённом убраны лишние ключевые слова.
            if(simplifiedSearchRequest){
                InputName=args[0];
                OutputName=args[1];
                ENCODING=args[2];
                ColumnToSearch=args[3];
                DataToSearch= args[4];
            }else{
                InputName = args[1];
                OutputName = args[3];
                ENCODING = args[5];
                ColumnToSearch = args[7];
                DataToSearch = args[9];
            }

            convertDataToCells();   // Производим запись в ячейки данных  а также проверку если это возможно

            if(printListToConsole)CustomSupport.printList(dataList);                                      // Выводим записанный список в консоль
            CustomSupport.searchFor(ColumnToSearch,DataToSearch);                   // Производим поиск значения(ний) и запись в текстовый файл
            System.out.println("Ошибок в типах данных: "+ WrongDataErrorCounter);   // Просто сообщение об ошибках в типах данных

        }else{
            System.out.println("No arguments, try again");
        }
    }

    private static void convertDataToCells() throws FileNotFoundException {
        List<DataCell> sublist=new ArrayList<>();
        Scanner scanner = new Scanner(new File(InputName));
        //Scanner scanner = new Scanner(new File("table2.csv"));  //Это на случай если хотим быстро запустить без аргументов. Также не забываем закомментировать лишнее.
        scanner.useDelimiter(DELIMITER);

        String dt=scanner.nextLine();

        String[] tkns = dt.split(DELIMITER);
        for(String a:tkns){
            String[]tmp=a.split(" ");
            DataCell cell = new DataCell(tmp[0], DataType.HEADING);
            cell.isTypeCorrect();   //Проверка на правильность данных
            sublist.add(cell);
            switch (tmp[1]){
                case "String":
                    dataTypeList.add(DataType.STRING);
                    cell.headerType= DataType.STRING;
                    break;
                case "Date":
                    dataTypeList.add(DataType.DATE);
                    cell.headerType= DataType.DATE;
                    break;
                case "Integer":
                    dataTypeList.add(DataType.INTEGER);
                    cell.headerType= DataType.INTEGER;
                    break;
                case "Float":
                    dataTypeList.add(DataType.FLOAT);
                    cell.headerType= DataType.FLOAT;
                    break;
                default:
                    System.out.println("Ошибка! Неизвестный тип данных. Это может повлечь за собой дальнейшие ошибки.");
                    dataTypeList.add(DataType.WRONG_TYPE);
                    cell.headerType= DataType.WRONG_TYPE;
                    break;
            }
        }
        dataList.add(sublist);

        while(scanner.hasNext()){
            String data=scanner.nextLine();
            String[]tokens=data.split(DELIMITER);
            sublist=new ArrayList<>();
            for(int i=0;i<tokens.length;i++){
                DataType dataType=dataTypeList.get(i);
                Object a = new Object();
                switch (dataType){
                    case STRING:
                    a=tokens[i]; //Здесь без проверок так как тип переменной изначально String.
                        //a.matches(".*\\d.*");  Интересная строчка кода. Вернёт true если в String'е будут цифры.
                        //Добавлять проверку на цифры не стал, так как для типа String нормально хранить числа внутри.
                        break;
                    case FLOAT:
                        try{a = Float.parseFloat(tokens[i]);
                        } catch (NumberFormatException e){
                            //e.printStackTrace();
                            dataType= DataType.WRONG_TYPE;
                            WrongDataErrorCounter++;
                            a=String.valueOf(tokens[i]);
                        }
                        break;
                    case INTEGER:
                        try{a = Integer.parseInt(tokens[i]);
                        } catch (NumberFormatException e){
                            dataType= DataType.WRONG_TYPE;
                            WrongDataErrorCounter++;
                            a=String.valueOf(tokens[i]);
                        }
                        break;
                    case DATE:
                        if(SimpleDate.checkIfDateIsWrong(tokens[i])){
                            dataType= DataType.WRONG_TYPE;
                            WrongDataErrorCounter++;
                            a=String.valueOf(tokens[i]);
                        }else{
                            a=new SimpleDate(tokens[i]);
                        }
                        break;
                    case WRONG_TYPE:
                        dataType= DataType.WRONG_TYPE;
                        WrongDataErrorCounter++;
                        a=String.valueOf(tokens[i]);
                        break;
                    default:
                        System.out.println("FATAL ERROR");
                        break;
                }
                DataCell cell = new DataCell(a,dataType);
                cell.isTypeCorrect();
                sublist.add(cell);
            }
            dataList.add(sublist);
        }
        scanner.close();
    }


}









