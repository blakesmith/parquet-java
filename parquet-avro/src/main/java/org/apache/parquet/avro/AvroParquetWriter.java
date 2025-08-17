/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.parquet.avro;

import java.io.IOException;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.specific.SpecificData;
import org.apache.parquet.column.ParquetProperties;
import org.apache.parquet.column.ParquetProperties.WriterVersion;
import org.apache.parquet.compression.CompressionCodecFactory;
import org.apache.parquet.conf.ParquetConfiguration;
import org.apache.parquet.crypto.FileEncryptionProperties;
import org.apache.parquet.hadoop.ParquetFileWriter;
import org.apache.parquet.hadoop.ParquetWriter;
import org.apache.parquet.hadoop.api.WriteSupport;
import org.apache.parquet.hadoop.metadata.CompressionCodecName;
import org.apache.parquet.io.OutputFile;

/**
 * Write Avro records to a Parquet file.
 */
public class AvroParquetWriter<T> extends ParquetWriter<T> {

  AvroParquetWriter(
      OutputFile file,
      ParquetFileWriter.Mode mode,
      WriteSupport<T> writeSupport,
      CompressionCodecName compressionCodecName,
      CompressionCodecFactory codecFactory,
      long rowGroupSize,
      boolean validating,
      ParquetConfiguration conf,
      int maxPaddingSize,
      ParquetProperties encodingProps,
      FileEncryptionProperties encryptionProperties)
      throws IOException {
    super(
      file,
      mode,
      writeSupport,
      compressionCodecName,
      codecFactory,
      rowGroupSize,
      validating,
      conf,
      maxPaddingSize,
      encodingProps,
      encryptionProperties);
  }

  public static <T> Builder<T> builder(OutputFile file) {
    return new Builder<T>(file);
  }

  private static <T> WriteSupport<T> writeSupport(Schema avroSchema, GenericData model) {
    return new AvroWriteSupport<T>(new AvroSchemaConverter().convert(avroSchema), avroSchema, model);
  }

  private static <T> WriteSupport<T> writeSupport(ParquetConfiguration conf, Schema avroSchema, GenericData model) {
    return new AvroWriteSupport<T>(new AvroSchemaConverter(conf).convert(avroSchema), avroSchema, model);
  }

  public static class Builder<T> extends ParquetWriter.Builder<T, Builder<T>> {
    private Schema schema = null;
    private GenericData model = null;

    private Builder(OutputFile file) {
      super(file);
    }

    public Builder<T> withSchema(Schema schema) {
      this.schema = schema;
      return this;
    }

    public Builder<T> withDataModel(GenericData model) {
      this.model = model;
      return this;
    }

    @Override
    protected Builder<T> self() {
      return this;
    }

    @Override
    protected WriteSupport<T> getWriteSupport(ParquetConfiguration conf) {
      return AvroParquetWriter.writeSupport(conf, schema, model);
    }
  }
}
