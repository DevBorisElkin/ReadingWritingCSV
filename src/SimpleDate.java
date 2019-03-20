/**
 * Кастомный класс для хранения и проверки правильности даты.
 */
class SimpleDate{
    Integer day, month, year;
    public SimpleDate(String date) { //  dd.mm.yyyy
        String[] tokens = date.split("\\.");
        day=Integer.parseInt(tokens[0]);
        month=Integer.parseInt(tokens[1]);
        year=Integer.parseInt(tokens[2]);
    }
    public String getDateStr(){
        String tmp="";
        if(day<10){
            tmp=day.toString();
            tmp="0"+tmp+".";
        }else{
            tmp=day.toString()+".";
        }
        String tmp2;
        if(month<10){
            tmp2=month.toString();
            tmp2="0"+tmp2+".";
        }else{
            tmp2=month.toString()+".";
        }

        String tmp3;
        if(year<1000){
            tmp3=year.toString();
            tmp3="0"+tmp3;
        }else{
            tmp3=year.toString();
        }

        return tmp+tmp2+tmp3;
    }
    @Override
    public String toString() {
        return getDateStr();
    }

    /**
     * Проверка на правильность даты. Год не может быть больше 2100 лет.
     * Месяц ограничен значением 12, день - значением 31.
     */
    public static boolean checkIfDateIsWrong(String a){
        boolean wrong=false;
        int a1=0;
        int a2=0;
        int a3=0;
        String[]tmp = a.split("\\.");
        if(tmp.length>1){
            try{a1 = Integer.parseInt(tmp[0]);
                a2 = Integer.parseInt(tmp[1]);
                a3 = Integer.parseInt(tmp[2]);
            } catch (NumberFormatException e){
                wrong=true;
            }

            if(a1>31){
                wrong=true;
            }
            if(a2>366){
                wrong=true;
            }
            if(a3>2100){
                wrong=true;
            }
            return wrong;
        }else{
            return true;
        }

    }
}