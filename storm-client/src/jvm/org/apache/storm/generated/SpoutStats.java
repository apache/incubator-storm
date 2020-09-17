/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * Autogenerated by Thrift Compiler (0.12.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package org.apache.storm.generated;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.12.0)")
public class SpoutStats implements org.apache.storm.thrift.TBase<SpoutStats, SpoutStats._Fields>, java.io.Serializable, Cloneable, Comparable<SpoutStats> {
  private static final org.apache.storm.thrift.protocol.TStruct STRUCT_DESC = new org.apache.storm.thrift.protocol.TStruct("SpoutStats");

  private static final org.apache.storm.thrift.protocol.TField ACKED_FIELD_DESC = new org.apache.storm.thrift.protocol.TField("acked", org.apache.storm.thrift.protocol.TType.MAP, (short)1);
  private static final org.apache.storm.thrift.protocol.TField FAILED_FIELD_DESC = new org.apache.storm.thrift.protocol.TField("failed", org.apache.storm.thrift.protocol.TType.MAP, (short)2);
  private static final org.apache.storm.thrift.protocol.TField COMPLETE_MS_AVG_FIELD_DESC = new org.apache.storm.thrift.protocol.TField("complete_ms_avg", org.apache.storm.thrift.protocol.TType.MAP, (short)3);

  private static final org.apache.storm.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new SpoutStatsStandardSchemeFactory();
  private static final org.apache.storm.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new SpoutStatsTupleSchemeFactory();

  private @org.apache.storm.thrift.annotation.Nullable java.util.Map<java.lang.String,java.util.Map<java.lang.String,java.lang.Long>> acked; // required
  private @org.apache.storm.thrift.annotation.Nullable java.util.Map<java.lang.String,java.util.Map<java.lang.String,java.lang.Long>> failed; // required
  private @org.apache.storm.thrift.annotation.Nullable java.util.Map<java.lang.String,java.util.Map<java.lang.String,java.lang.Double>> complete_ms_avg; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.storm.thrift.TFieldIdEnum {
    ACKED((short)1, "acked"),
    FAILED((short)2, "failed"),
    COMPLETE_MS_AVG((short)3, "complete_ms_avg");

    private static final java.util.Map<java.lang.String, _Fields> byName = new java.util.HashMap<java.lang.String, _Fields>();

    static {
      for (_Fields field : java.util.EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    @org.apache.storm.thrift.annotation.Nullable
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // ACKED
          return ACKED;
        case 2: // FAILED
          return FAILED;
        case 3: // COMPLETE_MS_AVG
          return COMPLETE_MS_AVG;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new java.lang.IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    @org.apache.storm.thrift.annotation.Nullable
    public static _Fields findByName(java.lang.String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final java.lang.String _fieldName;

    _Fields(short thriftId, java.lang.String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public java.lang.String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  public static final java.util.Map<_Fields, org.apache.storm.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.storm.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.storm.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.ACKED, new org.apache.storm.thrift.meta_data.FieldMetaData("acked", org.apache.storm.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.storm.thrift.meta_data.MapMetaData(org.apache.storm.thrift.protocol.TType.MAP, 
            new org.apache.storm.thrift.meta_data.FieldValueMetaData(org.apache.storm.thrift.protocol.TType.STRING), 
            new org.apache.storm.thrift.meta_data.MapMetaData(org.apache.storm.thrift.protocol.TType.MAP, 
                new org.apache.storm.thrift.meta_data.FieldValueMetaData(org.apache.storm.thrift.protocol.TType.STRING), 
                new org.apache.storm.thrift.meta_data.FieldValueMetaData(org.apache.storm.thrift.protocol.TType.I64)))));
    tmpMap.put(_Fields.FAILED, new org.apache.storm.thrift.meta_data.FieldMetaData("failed", org.apache.storm.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.storm.thrift.meta_data.MapMetaData(org.apache.storm.thrift.protocol.TType.MAP, 
            new org.apache.storm.thrift.meta_data.FieldValueMetaData(org.apache.storm.thrift.protocol.TType.STRING), 
            new org.apache.storm.thrift.meta_data.MapMetaData(org.apache.storm.thrift.protocol.TType.MAP, 
                new org.apache.storm.thrift.meta_data.FieldValueMetaData(org.apache.storm.thrift.protocol.TType.STRING), 
                new org.apache.storm.thrift.meta_data.FieldValueMetaData(org.apache.storm.thrift.protocol.TType.I64)))));
    tmpMap.put(_Fields.COMPLETE_MS_AVG, new org.apache.storm.thrift.meta_data.FieldMetaData("complete_ms_avg", org.apache.storm.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.storm.thrift.meta_data.MapMetaData(org.apache.storm.thrift.protocol.TType.MAP, 
            new org.apache.storm.thrift.meta_data.FieldValueMetaData(org.apache.storm.thrift.protocol.TType.STRING), 
            new org.apache.storm.thrift.meta_data.MapMetaData(org.apache.storm.thrift.protocol.TType.MAP, 
                new org.apache.storm.thrift.meta_data.FieldValueMetaData(org.apache.storm.thrift.protocol.TType.STRING), 
                new org.apache.storm.thrift.meta_data.FieldValueMetaData(org.apache.storm.thrift.protocol.TType.DOUBLE)))));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.storm.thrift.meta_data.FieldMetaData.addStructMetaDataMap(SpoutStats.class, metaDataMap);
  }

  public SpoutStats() {
  }

  public SpoutStats(
    java.util.Map<java.lang.String,java.util.Map<java.lang.String,java.lang.Long>> acked,
    java.util.Map<java.lang.String,java.util.Map<java.lang.String,java.lang.Long>> failed,
    java.util.Map<java.lang.String,java.util.Map<java.lang.String,java.lang.Double>> complete_ms_avg)
  {
    this();
    this.acked = acked;
    this.failed = failed;
    this.complete_ms_avg = complete_ms_avg;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public SpoutStats(SpoutStats other) {
    if (other.is_set_acked()) {
      java.util.Map<java.lang.String,java.util.Map<java.lang.String,java.lang.Long>> __this__acked = new java.util.HashMap<java.lang.String,java.util.Map<java.lang.String,java.lang.Long>>(other.acked.size());
      for (java.util.Map.Entry<java.lang.String, java.util.Map<java.lang.String,java.lang.Long>> other_element : other.acked.entrySet()) {

        java.lang.String other_element_key = other_element.getKey();
        java.util.Map<java.lang.String,java.lang.Long> other_element_value = other_element.getValue();

        java.lang.String __this__acked_copy_key = other_element_key;

        java.util.Map<java.lang.String,java.lang.Long> __this__acked_copy_value = new java.util.HashMap<java.lang.String,java.lang.Long>(other_element_value);

        __this__acked.put(__this__acked_copy_key, __this__acked_copy_value);
      }
      this.acked = __this__acked;
    }
    if (other.is_set_failed()) {
      java.util.Map<java.lang.String,java.util.Map<java.lang.String,java.lang.Long>> __this__failed = new java.util.HashMap<java.lang.String,java.util.Map<java.lang.String,java.lang.Long>>(other.failed.size());
      for (java.util.Map.Entry<java.lang.String, java.util.Map<java.lang.String,java.lang.Long>> other_element : other.failed.entrySet()) {

        java.lang.String other_element_key = other_element.getKey();
        java.util.Map<java.lang.String,java.lang.Long> other_element_value = other_element.getValue();

        java.lang.String __this__failed_copy_key = other_element_key;

        java.util.Map<java.lang.String,java.lang.Long> __this__failed_copy_value = new java.util.HashMap<java.lang.String,java.lang.Long>(other_element_value);

        __this__failed.put(__this__failed_copy_key, __this__failed_copy_value);
      }
      this.failed = __this__failed;
    }
    if (other.is_set_complete_ms_avg()) {
      java.util.Map<java.lang.String,java.util.Map<java.lang.String,java.lang.Double>> __this__complete_ms_avg = new java.util.HashMap<java.lang.String,java.util.Map<java.lang.String,java.lang.Double>>(other.complete_ms_avg.size());
      for (java.util.Map.Entry<java.lang.String, java.util.Map<java.lang.String,java.lang.Double>> other_element : other.complete_ms_avg.entrySet()) {

        java.lang.String other_element_key = other_element.getKey();
        java.util.Map<java.lang.String,java.lang.Double> other_element_value = other_element.getValue();

        java.lang.String __this__complete_ms_avg_copy_key = other_element_key;

        java.util.Map<java.lang.String,java.lang.Double> __this__complete_ms_avg_copy_value = new java.util.HashMap<java.lang.String,java.lang.Double>(other_element_value);

        __this__complete_ms_avg.put(__this__complete_ms_avg_copy_key, __this__complete_ms_avg_copy_value);
      }
      this.complete_ms_avg = __this__complete_ms_avg;
    }
  }

  public SpoutStats deepCopy() {
    return new SpoutStats(this);
  }

  @Override
  public void clear() {
    this.acked = null;
    this.failed = null;
    this.complete_ms_avg = null;
  }

  public int get_acked_size() {
    return (this.acked == null) ? 0 : this.acked.size();
  }

  public void put_to_acked(java.lang.String key, java.util.Map<java.lang.String,java.lang.Long> val) {
    if (this.acked == null) {
      this.acked = new java.util.HashMap<java.lang.String,java.util.Map<java.lang.String,java.lang.Long>>();
    }
    this.acked.put(key, val);
  }

  @org.apache.storm.thrift.annotation.Nullable
  public java.util.Map<java.lang.String,java.util.Map<java.lang.String,java.lang.Long>> get_acked() {
    return this.acked;
  }

  public void set_acked(@org.apache.storm.thrift.annotation.Nullable java.util.Map<java.lang.String,java.util.Map<java.lang.String,java.lang.Long>> acked) {
    this.acked = acked;
  }

  public void unset_acked() {
    this.acked = null;
  }

  /** Returns true if field acked is set (has been assigned a value) and false otherwise */
  public boolean is_set_acked() {
    return this.acked != null;
  }

  public void set_acked_isSet(boolean value) {
    if (!value) {
      this.acked = null;
    }
  }

  public int get_failed_size() {
    return (this.failed == null) ? 0 : this.failed.size();
  }

  public void put_to_failed(java.lang.String key, java.util.Map<java.lang.String,java.lang.Long> val) {
    if (this.failed == null) {
      this.failed = new java.util.HashMap<java.lang.String,java.util.Map<java.lang.String,java.lang.Long>>();
    }
    this.failed.put(key, val);
  }

  @org.apache.storm.thrift.annotation.Nullable
  public java.util.Map<java.lang.String,java.util.Map<java.lang.String,java.lang.Long>> get_failed() {
    return this.failed;
  }

  public void set_failed(@org.apache.storm.thrift.annotation.Nullable java.util.Map<java.lang.String,java.util.Map<java.lang.String,java.lang.Long>> failed) {
    this.failed = failed;
  }

  public void unset_failed() {
    this.failed = null;
  }

  /** Returns true if field failed is set (has been assigned a value) and false otherwise */
  public boolean is_set_failed() {
    return this.failed != null;
  }

  public void set_failed_isSet(boolean value) {
    if (!value) {
      this.failed = null;
    }
  }

  public int get_complete_ms_avg_size() {
    return (this.complete_ms_avg == null) ? 0 : this.complete_ms_avg.size();
  }

  public void put_to_complete_ms_avg(java.lang.String key, java.util.Map<java.lang.String,java.lang.Double> val) {
    if (this.complete_ms_avg == null) {
      this.complete_ms_avg = new java.util.HashMap<java.lang.String,java.util.Map<java.lang.String,java.lang.Double>>();
    }
    this.complete_ms_avg.put(key, val);
  }

  @org.apache.storm.thrift.annotation.Nullable
  public java.util.Map<java.lang.String,java.util.Map<java.lang.String,java.lang.Double>> get_complete_ms_avg() {
    return this.complete_ms_avg;
  }

  public void set_complete_ms_avg(@org.apache.storm.thrift.annotation.Nullable java.util.Map<java.lang.String,java.util.Map<java.lang.String,java.lang.Double>> complete_ms_avg) {
    this.complete_ms_avg = complete_ms_avg;
  }

  public void unset_complete_ms_avg() {
    this.complete_ms_avg = null;
  }

  /** Returns true if field complete_ms_avg is set (has been assigned a value) and false otherwise */
  public boolean is_set_complete_ms_avg() {
    return this.complete_ms_avg != null;
  }

  public void set_complete_ms_avg_isSet(boolean value) {
    if (!value) {
      this.complete_ms_avg = null;
    }
  }

  public void setFieldValue(_Fields field, @org.apache.storm.thrift.annotation.Nullable java.lang.Object value) {
    switch (field) {
    case ACKED:
      if (value == null) {
        unset_acked();
      } else {
        set_acked((java.util.Map<java.lang.String,java.util.Map<java.lang.String,java.lang.Long>>)value);
      }
      break;

    case FAILED:
      if (value == null) {
        unset_failed();
      } else {
        set_failed((java.util.Map<java.lang.String,java.util.Map<java.lang.String,java.lang.Long>>)value);
      }
      break;

    case COMPLETE_MS_AVG:
      if (value == null) {
        unset_complete_ms_avg();
      } else {
        set_complete_ms_avg((java.util.Map<java.lang.String,java.util.Map<java.lang.String,java.lang.Double>>)value);
      }
      break;

    }
  }

  @org.apache.storm.thrift.annotation.Nullable
  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case ACKED:
      return get_acked();

    case FAILED:
      return get_failed();

    case COMPLETE_MS_AVG:
      return get_complete_ms_avg();

    }
    throw new java.lang.IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new java.lang.IllegalArgumentException();
    }

    switch (field) {
    case ACKED:
      return is_set_acked();
    case FAILED:
      return is_set_failed();
    case COMPLETE_MS_AVG:
      return is_set_complete_ms_avg();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that == null)
      return false;
    if (that instanceof SpoutStats)
      return this.equals((SpoutStats)that);
    return false;
  }

  public boolean equals(SpoutStats that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_acked = true && this.is_set_acked();
    boolean that_present_acked = true && that.is_set_acked();
    if (this_present_acked || that_present_acked) {
      if (!(this_present_acked && that_present_acked))
        return false;
      if (!this.acked.equals(that.acked))
        return false;
    }

    boolean this_present_failed = true && this.is_set_failed();
    boolean that_present_failed = true && that.is_set_failed();
    if (this_present_failed || that_present_failed) {
      if (!(this_present_failed && that_present_failed))
        return false;
      if (!this.failed.equals(that.failed))
        return false;
    }

    boolean this_present_complete_ms_avg = true && this.is_set_complete_ms_avg();
    boolean that_present_complete_ms_avg = true && that.is_set_complete_ms_avg();
    if (this_present_complete_ms_avg || that_present_complete_ms_avg) {
      if (!(this_present_complete_ms_avg && that_present_complete_ms_avg))
        return false;
      if (!this.complete_ms_avg.equals(that.complete_ms_avg))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + ((is_set_acked()) ? 131071 : 524287);
    if (is_set_acked())
      hashCode = hashCode * 8191 + acked.hashCode();

    hashCode = hashCode * 8191 + ((is_set_failed()) ? 131071 : 524287);
    if (is_set_failed())
      hashCode = hashCode * 8191 + failed.hashCode();

    hashCode = hashCode * 8191 + ((is_set_complete_ms_avg()) ? 131071 : 524287);
    if (is_set_complete_ms_avg())
      hashCode = hashCode * 8191 + complete_ms_avg.hashCode();

    return hashCode;
  }

  @Override
  public int compareTo(SpoutStats other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = java.lang.Boolean.valueOf(is_set_acked()).compareTo(other.is_set_acked());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (is_set_acked()) {
      lastComparison = org.apache.storm.thrift.TBaseHelper.compareTo(this.acked, other.acked);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(is_set_failed()).compareTo(other.is_set_failed());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (is_set_failed()) {
      lastComparison = org.apache.storm.thrift.TBaseHelper.compareTo(this.failed, other.failed);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(is_set_complete_ms_avg()).compareTo(other.is_set_complete_ms_avg());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (is_set_complete_ms_avg()) {
      lastComparison = org.apache.storm.thrift.TBaseHelper.compareTo(this.complete_ms_avg, other.complete_ms_avg);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  @org.apache.storm.thrift.annotation.Nullable
  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.storm.thrift.protocol.TProtocol iprot) throws org.apache.storm.thrift.TException {
    scheme(iprot).read(iprot, this);
  }

  public void write(org.apache.storm.thrift.protocol.TProtocol oprot) throws org.apache.storm.thrift.TException {
    scheme(oprot).write(oprot, this);
  }

  @Override
  public java.lang.String toString() {
    java.lang.StringBuilder sb = new java.lang.StringBuilder("SpoutStats(");
    boolean first = true;

    sb.append("acked:");
    if (this.acked == null) {
      sb.append("null");
    } else {
      sb.append(this.acked);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("failed:");
    if (this.failed == null) {
      sb.append("null");
    } else {
      sb.append(this.failed);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("complete_ms_avg:");
    if (this.complete_ms_avg == null) {
      sb.append("null");
    } else {
      sb.append(this.complete_ms_avg);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.storm.thrift.TException {
    // check for required fields
    if (!is_set_acked()) {
      throw new org.apache.storm.thrift.protocol.TProtocolException("Required field 'acked' is unset! Struct:" + toString());
    }

    if (!is_set_failed()) {
      throw new org.apache.storm.thrift.protocol.TProtocolException("Required field 'failed' is unset! Struct:" + toString());
    }

    if (!is_set_complete_ms_avg()) {
      throw new org.apache.storm.thrift.protocol.TProtocolException("Required field 'complete_ms_avg' is unset! Struct:" + toString());
    }

    // check for sub-struct validity
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.storm.thrift.protocol.TCompactProtocol(new org.apache.storm.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.storm.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, java.lang.ClassNotFoundException {
    try {
      read(new org.apache.storm.thrift.protocol.TCompactProtocol(new org.apache.storm.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.storm.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class SpoutStatsStandardSchemeFactory implements org.apache.storm.thrift.scheme.SchemeFactory {
    public SpoutStatsStandardScheme getScheme() {
      return new SpoutStatsStandardScheme();
    }
  }

  private static class SpoutStatsStandardScheme extends org.apache.storm.thrift.scheme.StandardScheme<SpoutStats> {

    public void read(org.apache.storm.thrift.protocol.TProtocol iprot, SpoutStats struct) throws org.apache.storm.thrift.TException {
      org.apache.storm.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.storm.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // ACKED
            if (schemeField.type == org.apache.storm.thrift.protocol.TType.MAP) {
              {
                org.apache.storm.thrift.protocol.TMap _map290 = iprot.readMapBegin();
                struct.acked = new java.util.HashMap<java.lang.String,java.util.Map<java.lang.String,java.lang.Long>>(2*_map290.size);
                @org.apache.storm.thrift.annotation.Nullable java.lang.String _key291;
                @org.apache.storm.thrift.annotation.Nullable java.util.Map<java.lang.String,java.lang.Long> _val292;
                for (int _i293 = 0; _i293 < _map290.size; ++_i293)
                {
                  _key291 = iprot.readString();
                  {
                    org.apache.storm.thrift.protocol.TMap _map294 = iprot.readMapBegin();
                    _val292 = new java.util.HashMap<java.lang.String,java.lang.Long>(2*_map294.size);
                    @org.apache.storm.thrift.annotation.Nullable java.lang.String _key295;
                    long _val296;
                    for (int _i297 = 0; _i297 < _map294.size; ++_i297)
                    {
                      _key295 = iprot.readString();
                      _val296 = iprot.readI64();
                      _val292.put(_key295, _val296);
                    }
                    iprot.readMapEnd();
                  }
                  struct.acked.put(_key291, _val292);
                }
                iprot.readMapEnd();
              }
              struct.set_acked_isSet(true);
            } else { 
              org.apache.storm.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // FAILED
            if (schemeField.type == org.apache.storm.thrift.protocol.TType.MAP) {
              {
                org.apache.storm.thrift.protocol.TMap _map298 = iprot.readMapBegin();
                struct.failed = new java.util.HashMap<java.lang.String,java.util.Map<java.lang.String,java.lang.Long>>(2*_map298.size);
                @org.apache.storm.thrift.annotation.Nullable java.lang.String _key299;
                @org.apache.storm.thrift.annotation.Nullable java.util.Map<java.lang.String,java.lang.Long> _val300;
                for (int _i301 = 0; _i301 < _map298.size; ++_i301)
                {
                  _key299 = iprot.readString();
                  {
                    org.apache.storm.thrift.protocol.TMap _map302 = iprot.readMapBegin();
                    _val300 = new java.util.HashMap<java.lang.String,java.lang.Long>(2*_map302.size);
                    @org.apache.storm.thrift.annotation.Nullable java.lang.String _key303;
                    long _val304;
                    for (int _i305 = 0; _i305 < _map302.size; ++_i305)
                    {
                      _key303 = iprot.readString();
                      _val304 = iprot.readI64();
                      _val300.put(_key303, _val304);
                    }
                    iprot.readMapEnd();
                  }
                  struct.failed.put(_key299, _val300);
                }
                iprot.readMapEnd();
              }
              struct.set_failed_isSet(true);
            } else { 
              org.apache.storm.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // COMPLETE_MS_AVG
            if (schemeField.type == org.apache.storm.thrift.protocol.TType.MAP) {
              {
                org.apache.storm.thrift.protocol.TMap _map306 = iprot.readMapBegin();
                struct.complete_ms_avg = new java.util.HashMap<java.lang.String,java.util.Map<java.lang.String,java.lang.Double>>(2*_map306.size);
                @org.apache.storm.thrift.annotation.Nullable java.lang.String _key307;
                @org.apache.storm.thrift.annotation.Nullable java.util.Map<java.lang.String,java.lang.Double> _val308;
                for (int _i309 = 0; _i309 < _map306.size; ++_i309)
                {
                  _key307 = iprot.readString();
                  {
                    org.apache.storm.thrift.protocol.TMap _map310 = iprot.readMapBegin();
                    _val308 = new java.util.HashMap<java.lang.String,java.lang.Double>(2*_map310.size);
                    @org.apache.storm.thrift.annotation.Nullable java.lang.String _key311;
                    double _val312;
                    for (int _i313 = 0; _i313 < _map310.size; ++_i313)
                    {
                      _key311 = iprot.readString();
                      _val312 = iprot.readDouble();
                      _val308.put(_key311, _val312);
                    }
                    iprot.readMapEnd();
                  }
                  struct.complete_ms_avg.put(_key307, _val308);
                }
                iprot.readMapEnd();
              }
              struct.set_complete_ms_avg_isSet(true);
            } else { 
              org.apache.storm.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.storm.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();
      struct.validate();
    }

    public void write(org.apache.storm.thrift.protocol.TProtocol oprot, SpoutStats struct) throws org.apache.storm.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.acked != null) {
        oprot.writeFieldBegin(ACKED_FIELD_DESC);
        {
          oprot.writeMapBegin(new org.apache.storm.thrift.protocol.TMap(org.apache.storm.thrift.protocol.TType.STRING, org.apache.storm.thrift.protocol.TType.MAP, struct.acked.size()));
          for (java.util.Map.Entry<java.lang.String, java.util.Map<java.lang.String,java.lang.Long>> _iter314 : struct.acked.entrySet())
          {
            oprot.writeString(_iter314.getKey());
            {
              oprot.writeMapBegin(new org.apache.storm.thrift.protocol.TMap(org.apache.storm.thrift.protocol.TType.STRING, org.apache.storm.thrift.protocol.TType.I64, _iter314.getValue().size()));
              for (java.util.Map.Entry<java.lang.String, java.lang.Long> _iter315 : _iter314.getValue().entrySet())
              {
                oprot.writeString(_iter315.getKey());
                oprot.writeI64(_iter315.getValue());
              }
              oprot.writeMapEnd();
            }
          }
          oprot.writeMapEnd();
        }
        oprot.writeFieldEnd();
      }
      if (struct.failed != null) {
        oprot.writeFieldBegin(FAILED_FIELD_DESC);
        {
          oprot.writeMapBegin(new org.apache.storm.thrift.protocol.TMap(org.apache.storm.thrift.protocol.TType.STRING, org.apache.storm.thrift.protocol.TType.MAP, struct.failed.size()));
          for (java.util.Map.Entry<java.lang.String, java.util.Map<java.lang.String,java.lang.Long>> _iter316 : struct.failed.entrySet())
          {
            oprot.writeString(_iter316.getKey());
            {
              oprot.writeMapBegin(new org.apache.storm.thrift.protocol.TMap(org.apache.storm.thrift.protocol.TType.STRING, org.apache.storm.thrift.protocol.TType.I64, _iter316.getValue().size()));
              for (java.util.Map.Entry<java.lang.String, java.lang.Long> _iter317 : _iter316.getValue().entrySet())
              {
                oprot.writeString(_iter317.getKey());
                oprot.writeI64(_iter317.getValue());
              }
              oprot.writeMapEnd();
            }
          }
          oprot.writeMapEnd();
        }
        oprot.writeFieldEnd();
      }
      if (struct.complete_ms_avg != null) {
        oprot.writeFieldBegin(COMPLETE_MS_AVG_FIELD_DESC);
        {
          oprot.writeMapBegin(new org.apache.storm.thrift.protocol.TMap(org.apache.storm.thrift.protocol.TType.STRING, org.apache.storm.thrift.protocol.TType.MAP, struct.complete_ms_avg.size()));
          for (java.util.Map.Entry<java.lang.String, java.util.Map<java.lang.String,java.lang.Double>> _iter318 : struct.complete_ms_avg.entrySet())
          {
            oprot.writeString(_iter318.getKey());
            {
              oprot.writeMapBegin(new org.apache.storm.thrift.protocol.TMap(org.apache.storm.thrift.protocol.TType.STRING, org.apache.storm.thrift.protocol.TType.DOUBLE, _iter318.getValue().size()));
              for (java.util.Map.Entry<java.lang.String, java.lang.Double> _iter319 : _iter318.getValue().entrySet())
              {
                oprot.writeString(_iter319.getKey());
                oprot.writeDouble(_iter319.getValue());
              }
              oprot.writeMapEnd();
            }
          }
          oprot.writeMapEnd();
        }
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class SpoutStatsTupleSchemeFactory implements org.apache.storm.thrift.scheme.SchemeFactory {
    public SpoutStatsTupleScheme getScheme() {
      return new SpoutStatsTupleScheme();
    }
  }

  private static class SpoutStatsTupleScheme extends org.apache.storm.thrift.scheme.TupleScheme<SpoutStats> {

    @Override
    public void write(org.apache.storm.thrift.protocol.TProtocol prot, SpoutStats struct) throws org.apache.storm.thrift.TException {
      org.apache.storm.thrift.protocol.TTupleProtocol oprot = (org.apache.storm.thrift.protocol.TTupleProtocol) prot;
      {
        oprot.writeI32(struct.acked.size());
        for (java.util.Map.Entry<java.lang.String, java.util.Map<java.lang.String,java.lang.Long>> _iter320 : struct.acked.entrySet())
        {
          oprot.writeString(_iter320.getKey());
          {
            oprot.writeI32(_iter320.getValue().size());
            for (java.util.Map.Entry<java.lang.String, java.lang.Long> _iter321 : _iter320.getValue().entrySet())
            {
              oprot.writeString(_iter321.getKey());
              oprot.writeI64(_iter321.getValue());
            }
          }
        }
      }
      {
        oprot.writeI32(struct.failed.size());
        for (java.util.Map.Entry<java.lang.String, java.util.Map<java.lang.String,java.lang.Long>> _iter322 : struct.failed.entrySet())
        {
          oprot.writeString(_iter322.getKey());
          {
            oprot.writeI32(_iter322.getValue().size());
            for (java.util.Map.Entry<java.lang.String, java.lang.Long> _iter323 : _iter322.getValue().entrySet())
            {
              oprot.writeString(_iter323.getKey());
              oprot.writeI64(_iter323.getValue());
            }
          }
        }
      }
      {
        oprot.writeI32(struct.complete_ms_avg.size());
        for (java.util.Map.Entry<java.lang.String, java.util.Map<java.lang.String,java.lang.Double>> _iter324 : struct.complete_ms_avg.entrySet())
        {
          oprot.writeString(_iter324.getKey());
          {
            oprot.writeI32(_iter324.getValue().size());
            for (java.util.Map.Entry<java.lang.String, java.lang.Double> _iter325 : _iter324.getValue().entrySet())
            {
              oprot.writeString(_iter325.getKey());
              oprot.writeDouble(_iter325.getValue());
            }
          }
        }
      }
    }

    @Override
    public void read(org.apache.storm.thrift.protocol.TProtocol prot, SpoutStats struct) throws org.apache.storm.thrift.TException {
      org.apache.storm.thrift.protocol.TTupleProtocol iprot = (org.apache.storm.thrift.protocol.TTupleProtocol) prot;
      {
        org.apache.storm.thrift.protocol.TMap _map326 = new org.apache.storm.thrift.protocol.TMap(org.apache.storm.thrift.protocol.TType.STRING, org.apache.storm.thrift.protocol.TType.MAP, iprot.readI32());
        struct.acked = new java.util.HashMap<java.lang.String,java.util.Map<java.lang.String,java.lang.Long>>(2*_map326.size);
        @org.apache.storm.thrift.annotation.Nullable java.lang.String _key327;
        @org.apache.storm.thrift.annotation.Nullable java.util.Map<java.lang.String,java.lang.Long> _val328;
        for (int _i329 = 0; _i329 < _map326.size; ++_i329)
        {
          _key327 = iprot.readString();
          {
            org.apache.storm.thrift.protocol.TMap _map330 = new org.apache.storm.thrift.protocol.TMap(org.apache.storm.thrift.protocol.TType.STRING, org.apache.storm.thrift.protocol.TType.I64, iprot.readI32());
            _val328 = new java.util.HashMap<java.lang.String,java.lang.Long>(2*_map330.size);
            @org.apache.storm.thrift.annotation.Nullable java.lang.String _key331;
            long _val332;
            for (int _i333 = 0; _i333 < _map330.size; ++_i333)
            {
              _key331 = iprot.readString();
              _val332 = iprot.readI64();
              _val328.put(_key331, _val332);
            }
          }
          struct.acked.put(_key327, _val328);
        }
      }
      struct.set_acked_isSet(true);
      {
        org.apache.storm.thrift.protocol.TMap _map334 = new org.apache.storm.thrift.protocol.TMap(org.apache.storm.thrift.protocol.TType.STRING, org.apache.storm.thrift.protocol.TType.MAP, iprot.readI32());
        struct.failed = new java.util.HashMap<java.lang.String,java.util.Map<java.lang.String,java.lang.Long>>(2*_map334.size);
        @org.apache.storm.thrift.annotation.Nullable java.lang.String _key335;
        @org.apache.storm.thrift.annotation.Nullable java.util.Map<java.lang.String,java.lang.Long> _val336;
        for (int _i337 = 0; _i337 < _map334.size; ++_i337)
        {
          _key335 = iprot.readString();
          {
            org.apache.storm.thrift.protocol.TMap _map338 = new org.apache.storm.thrift.protocol.TMap(org.apache.storm.thrift.protocol.TType.STRING, org.apache.storm.thrift.protocol.TType.I64, iprot.readI32());
            _val336 = new java.util.HashMap<java.lang.String,java.lang.Long>(2*_map338.size);
            @org.apache.storm.thrift.annotation.Nullable java.lang.String _key339;
            long _val340;
            for (int _i341 = 0; _i341 < _map338.size; ++_i341)
            {
              _key339 = iprot.readString();
              _val340 = iprot.readI64();
              _val336.put(_key339, _val340);
            }
          }
          struct.failed.put(_key335, _val336);
        }
      }
      struct.set_failed_isSet(true);
      {
        org.apache.storm.thrift.protocol.TMap _map342 = new org.apache.storm.thrift.protocol.TMap(org.apache.storm.thrift.protocol.TType.STRING, org.apache.storm.thrift.protocol.TType.MAP, iprot.readI32());
        struct.complete_ms_avg = new java.util.HashMap<java.lang.String,java.util.Map<java.lang.String,java.lang.Double>>(2*_map342.size);
        @org.apache.storm.thrift.annotation.Nullable java.lang.String _key343;
        @org.apache.storm.thrift.annotation.Nullable java.util.Map<java.lang.String,java.lang.Double> _val344;
        for (int _i345 = 0; _i345 < _map342.size; ++_i345)
        {
          _key343 = iprot.readString();
          {
            org.apache.storm.thrift.protocol.TMap _map346 = new org.apache.storm.thrift.protocol.TMap(org.apache.storm.thrift.protocol.TType.STRING, org.apache.storm.thrift.protocol.TType.DOUBLE, iprot.readI32());
            _val344 = new java.util.HashMap<java.lang.String,java.lang.Double>(2*_map346.size);
            @org.apache.storm.thrift.annotation.Nullable java.lang.String _key347;
            double _val348;
            for (int _i349 = 0; _i349 < _map346.size; ++_i349)
            {
              _key347 = iprot.readString();
              _val348 = iprot.readDouble();
              _val344.put(_key347, _val348);
            }
          }
          struct.complete_ms_avg.put(_key343, _val344);
        }
      }
      struct.set_complete_ms_avg_isSet(true);
    }
  }

  private static <S extends org.apache.storm.thrift.scheme.IScheme> S scheme(org.apache.storm.thrift.protocol.TProtocol proto) {
    return (org.apache.storm.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

