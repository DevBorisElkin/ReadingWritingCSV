import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/** Справка по args:
 *  пример вводимой в консоль строки: java –jar search_csv.jar –in in_file.csv –out out_file.csv -enc UTF-8 -col Дата_рождения -exp 18.06.1983
 *                                                              0     1        2        3        4    5      6       7          8      9
 *
 *  Сейчас включен упрощённый ввод данных.
 *  1 - Имя файла на подачу
 *  2 - Имя файла на вывод
 *  3 - Имя столбика, который ищем
 *  4 - Данные, которые ищем
*/

@SuppressWarnings("all")
public class Main {
    /**
     * Разделитель, который используется для csv файла
     */
    public static final String DELIMITER = ";";

    /** Кол-во пробелов - используется для
     * красивого вывода в консоль. В случае если выводимое значение слишком
     * большое, необходимо увелисить это поле*/
    public static final int SPACE_GAP=30;

    /**Стандарные значения, меняются на введённые  */
    public static String InputName = "input.csv";
    public static String OutputName = "output.csv";
    public static String ENCODING = "utf-8";
    public static String ColumnToSearch;
    public static String DataToSearch;

    /** Если True, используется упрощённый ввод через консоль.
     * Если false - ввод как в примере из задания. */
    public static boolean simplifiedSearchRequest = true;

    /** Если True, выведет ВСЕ значения в нужной колонке
     *  по поисковому запросу, если False
     *  - только первое(как по заданию). */
    public static boolean multimpleResults = true;

    /**
     * Протсо вывод в консоль. Слишком большие
     * таблицы выводиться не будут
     */
    public static boolean printListToConsole = true;

    /** Можем контролировать предел для выводимой информации в консоль  */
    public static final int OUTPUT_COLUMNS = 40;
    public static final int OUTPUT_ROWS = 40;

    /** Используется для создания или пересоздания файла вывода  */
    public static boolean oneTimeRefreshingFile=true;

    /**  Счётчик для ошибок в типах подаваемых данных.
     * На этот параметр также влияют ошибки шапке каждой колонки -
     * если тип не сущесвтует, все данные будут считаться ошибочными.
     * Пример: вместо колонки "Name String" написано "Name Strink" -
     * - все поля будут считаться с ошибочным типом.*/
    public static int WrongDataErrorCounter = 0;

    /**
     * Массив массивов для записи данных при считывании. Каждая ячейка таблицы -
     * - отдельный DataCell
     */
    public static List<List<DataCell>> dataList = new ArrayList<>();

    /**
     * Enum - у каждого DataCell свой тип. 4 - типы данных. Heading - для "шапок" каждой колонки,
     * Wrong_Type - используется в случае возникновения ошибки, NONE - исполькуется временно.
     */
    public enum DataType { STRING, DATE, INTEGER, FLOAT, HEADING, WRONG_TYPE, NONE}

    /**
     * В этом массиве хранятся данные о типах различных столбцов.
     */
    public static List<DataType> dataTypeList = new ArrayList<>();


    /**
     * Точка входа в программу.
     * @param args передаём массиво входных параметров в метод action.
     */
    public static void main(String[] args) throws FileNotFoundException{
        action(args);
    }

    private static void action(String[]args) throws FileNotFoundException {

        /**Проверка на наличие аргументов.*/
        if(args.length>0){
            if(args[0].toLowerCase().equals("help")) {
                help();
            }else{
                /**Здесь проверяем, усложнённый ли поисковый запрос,
                 *  или упрощённый. В урощённом убраны лишние ключевые слова.*/
                try{
                    if(simplifiedSearchRequest){
                        InputName=args[0];    //Это урощённый вариант.
                        OutputName=args[1];
                        ColumnToSearch=args[2];
                        DataToSearch= args[3];
                    }else{
                        InputName = args[1];
                        OutputName = args[3];
                        ENCODING = args[5];
                        ColumnToSearch = args[7];
                        DataToSearch = args[9];
                    }
                }catch (Exception e){
                    System.out.println("Ошибка ввода данных. Прекращение работы программы. Введите \"Help\" для получения помощи.");
                    System.exit(1);
                }
                /** Производим запись в ячейки данных
                 * а также проверям на ошибки*/
                convertDataToCells();

                /** Выводим записанный список в консоль, если размер позволяет **/
                if(printListToConsole)CustomSupport.printList(dataList);

                /** / Производим поиск значения(ний) и запись в текстовый файл **/
                CustomSupport.searchFor(ColumnToSearch,DataToSearch);

                /** Выводим кол-во ошибок в типах данных  **/
                System.out.println("Ошибок в типах данных: "+ WrongDataErrorCounter);
            }
        }else{
            System.out.println("No arguments, try again");
        }
    }


    /**
     * В этом методе мы определяем тип для каждого столбца, записываем информацию о типах данных
     * этих столбцов, затем записываем информацию в ячейки
     * @see DataCell
     * эти ячейки записываем в
     * линейные списки, и передаём эти списки на хранение в основной массив
     */
    private static void convertDataToCells() throws FileNotFoundException {
        List<DataCell> sublist=new ArrayList<>();
        Scanner scanner = new Scanner(new File(InputName));
        //Scanner scanner = new Scanner(new File("table2.csv"));  //Это на случай если хотим быстро запустить без аргументов. Также не забываем закомментировать лишнее.
        scanner.useDelimiter(DELIMITER);

        /**
         * Здесь Сканнером была взята только 1 строка из csv файла
         */
        String dt=scanner.nextLine();

        String[] tkns = dt.split(DELIMITER);
        for(String a:tkns){
            String[]tmp=a.split(" ");
            DataCell cell = new DataCell(tmp[0], DataType.HEADING);

            /** Проверка на правильность типа в заголовках  */
            cell.isTypeCorrect();

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

        /** Полностью прошли по верхней строчке из cvs файла. */
        dataList.add(sublist);

        /**
         *  Проходимся по всем остальным данным и сверяем тип данных с записанным типом.
         */
        while(scanner.hasNext()){
            String data=scanner.nextLine();
            String[]tokens=data.split(DELIMITER);
            sublist=new ArrayList<>();
            for(int i=0;i<tokens.length;i++){
                DataType dataType=dataTypeList.get(i);
                Object a = new Object();
                switch (dataType){

                    /** Здесь без проверок так как тип переменной изначально String.
                      * a.matches(".*\\d.*");  Интересная строчка кода. Вернёт true если в String'е будут цифры.
                      * Добавлять проверку на цифры не стал, так как для типа String нормально хранить числа внутри.
                      * Для универсальности оставил без проверки. В некоторых названиях могут присутствовать цифры.
                     ***/
                    case STRING:
                    a=tokens[i];
                        break;

                    /**
                     * В случае если тип int, он будет конвертирован во float.
                     * Если не получится произвести конвертацию из-за ошибки, данные будут записаны
                     * в ячейку в виде String. Тип данных будет отображаться как ошибочный.
                     */
                    case FLOAT:
                        try{a = Float.parseFloat(tokens[i]);
                        } catch (NumberFormatException e){
                            dataType= DataType.WRONG_TYPE;
                            WrongDataErrorCounter++;
                            a=String.valueOf(tokens[i]);
                        }
                        break;

                    /**
                     * Почти тоже самое, только для integer'а
                     */
                    case INTEGER:
                        try{a = Integer.parseInt(tokens[i]);
                        } catch (NumberFormatException e){
                            dataType= DataType.WRONG_TYPE;
                            WrongDataErrorCounter++;
                            a=String.valueOf(tokens[i]);
                        }
                        break;

                    /**
                     * Простая проверка Даты через кастомный класс
                     * Также устраняются такие недочёты
                     * -> 2.5.2016 будет превращена в 02.05.2016
                     * @see SimpleDate
                     */
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

                /** Проверка типа на корректность.*/
                cell.isTypeCorrect();

                /**  Добавляем ячейку в список   */
                sublist.add(cell);
            }
            /**
             * Добавляем список в массив списков.
             * В итоге получаем такую структуру хранения данных
             *   ------------
             *   ------------
             *   ------------
             *   ------------
             */
            dataList.add(sublist);
        }
        scanner.close();
    }

    private static void help() {
        System.out.println("Правильный порядок для поиска данных:");
        System.out.println("1) Имя файла ввода");
        System.out.println("2) Имя файла вывода");
        System.out.println("3) Столбик для поиска информации");
        System.out.println("4) Данные для поиска");
        System.out.println("Пример полной команды запуска:java –jar gen_csv.jar in.csv out.csv country USA");
    }


}









