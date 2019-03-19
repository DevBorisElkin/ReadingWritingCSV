class DataCell<T> {
    T data;
    Main.DataType dataType;
    public Main.DataType headerType;

    public DataCell(T data, Main.DataType dataType) {
        this.data = data;
        this.dataType = dataType;
        headerType=Main.DataType.NONE;
    }

    public T getData() {
        return data;
    }

    public Main.DataType getDataType() {
        return dataType;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setDataType(Main.DataType dataType) {
        this.dataType = dataType;
    }

    public void isTypeCorrect() {
        boolean correct = false;
        T dataTmp = data;
        if (dataTmp.getClass() == Integer.class && getDataType() == Main.DataType.INTEGER) {
            correct = true;
        }
        if (dataTmp.getClass() == String.class && getDataType() == Main.DataType.STRING) {
            correct = true;
        }
        if (dataTmp.getClass() == SimpleDate.class && getDataType() == Main.DataType.DATE) {
            correct = true;
        }
        if (dataTmp.getClass() == Float.class && getDataType() == Main.DataType.FLOAT) {
            correct = true;
        }
        if (dataTmp.getClass() == String.class && getDataType() == Main.DataType.HEADING) {
            correct = true;
        }
        if (!correct) {
            setDataType(Main.DataType.WRONG_TYPE);
        }
    }
}