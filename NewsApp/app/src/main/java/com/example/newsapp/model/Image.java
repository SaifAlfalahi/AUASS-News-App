// automatically generated by the FlatBuffers compiler, do not modify

package com.example.newsapp.model;

import com.google.flatbuffers.BaseVector;
import com.google.flatbuffers.BooleanVector;
import com.google.flatbuffers.ByteVector;
import com.google.flatbuffers.Constants;
import com.google.flatbuffers.DoubleVector;
import com.google.flatbuffers.FlatBufferBuilder;
import com.google.flatbuffers.FloatVector;
import com.google.flatbuffers.IntVector;
import com.google.flatbuffers.LongVector;
import com.google.flatbuffers.ShortVector;
import com.google.flatbuffers.StringVector;
import com.google.flatbuffers.Struct;
import com.google.flatbuffers.Table;
import com.google.flatbuffers.UnionVector;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@SuppressWarnings("unused")
public final class Image extends Table {
  public static void ValidateVersion() { Constants.FLATBUFFERS_23_5_26(); }
  public static Image getRootAsImage(ByteBuffer _bb) { return getRootAsImage(_bb, new Image()); }
  public static Image getRootAsImage(ByteBuffer _bb, Image obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public void __init(int _i, ByteBuffer _bb) { __reset(_i, _bb); }
  public Image __assign(int _i, ByteBuffer _bb) { __init(_i, _bb); return this; }

  public int bytes(int j) { int o = __offset(4); return o != 0 ? bb.get(__vector(o) + j * 1) & 0xFF : 0; }
  public int bytesLength() { int o = __offset(4); return o != 0 ? __vector_len(o) : 0; }
  public ByteVector bytesVector() { return bytesVector(new ByteVector()); }
  public ByteVector bytesVector(ByteVector obj) { int o = __offset(4); return o != 0 ? obj.__assign(__vector(o), bb) : null; }
  public ByteBuffer bytesAsByteBuffer() { return __vector_as_bytebuffer(4, 1); }
  public ByteBuffer bytesInByteBuffer(ByteBuffer _bb) { return __vector_in_bytebuffer(_bb, 4, 1); }

  public static int createImage(FlatBufferBuilder builder,
      int bytesOffset) {
    builder.startTable(1);
    Image.addBytes(builder, bytesOffset);
    return Image.endImage(builder);
  }

  public static void startImage(FlatBufferBuilder builder) { builder.startTable(1); }
  public static void addBytes(FlatBufferBuilder builder, int bytesOffset) { builder.addOffset(0, bytesOffset, 0); }
  public static int createBytesVector(FlatBufferBuilder builder, byte[] data) { return builder.createByteVector(data); }
  public static int createBytesVector(FlatBufferBuilder builder, ByteBuffer data) { return builder.createByteVector(data); }
  public static void startBytesVector(FlatBufferBuilder builder, int numElems) { builder.startVector(1, numElems, 1); }
  public static int endImage(FlatBufferBuilder builder) {
    int o = builder.endTable();
    return o;
  }

  public static final class Vector extends BaseVector {
    public Vector __assign(int _vector, int _element_size, ByteBuffer _bb) { __reset(_vector, _element_size, _bb); return this; }

    public Image get(int j) { return get(new Image(), j); }
    public Image get(Image obj, int j) {  return obj.__assign(__indirect(__element(j), bb), bb); }
  }
}

