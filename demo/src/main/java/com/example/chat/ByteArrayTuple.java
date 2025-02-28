package com.example.chat;
class ByteArrayTuple 
{
    private final int type; 
    private final int size;
    private final byte[] data;
    
    public ByteArrayTuple(byte[] data, int type) 
    {
        this.size = data.length;
        this.data = data;
        this.type = type;
    }

    public int get_type()
    {
        return type;
    }
    
    public int getSize() 
    {
        return size;
    }
    
    public byte[] getData() 
    {
        return data;
    }
}