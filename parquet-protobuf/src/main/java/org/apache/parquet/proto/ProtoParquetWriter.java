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
package org.apache.parquet.proto;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;
import java.io.IOException;
import org.apache.parquet.column.ParquetProperties;
import org.apache.parquet.compression.CompressionCodecFactory;
import org.apache.parquet.conf.ParquetConfiguration;
import org.apache.parquet.crypto.FileEncryptionProperties;
import org.apache.parquet.hadoop.ParquetFileWriter;
import org.apache.parquet.hadoop.ParquetWriter;
import org.apache.parquet.hadoop.api.WriteSupport;
import org.apache.parquet.hadoop.metadata.CompressionCodecName;
import org.apache.parquet.io.OutputFile;

/**
 * Write Protobuf records to a Parquet file.
 */
public class ProtoParquetWriter<T extends MessageOrBuilder> extends ParquetWriter<T> {

  ProtoParquetWriter(
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

  private static <T extends MessageOrBuilder> WriteSupport<T> writeSupport(Class<? extends Message> protoMessage) {
    return new ProtoWriteSupport<>(protoMessage);
  }

  private static <T extends MessageOrBuilder> WriteSupport<T> writeSupport(Descriptors.Descriptor descriptor) {
    return new ProtoWriteSupport<>(descriptor);
  }

  public static class Builder<T> extends ParquetWriter.Builder<T, Builder<T>> {
    Class<? extends Message> protoMessage = null;

    private Descriptors.Descriptor descriptor = null;

    private Builder(OutputFile file) {
      super(file);
    }

    protected Builder<T> self() {
      return this;
    }

    public Builder<T> withMessage(Class<? extends Message> protoMessage) {
      this.protoMessage = protoMessage;
      return this;
    }

    public Builder<T> withDescriptor(Descriptors.Descriptor descriptor) {
      this.descriptor = descriptor;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected WriteSupport<T> getWriteSupport(ParquetConfiguration conf) {
      if (this.descriptor != null) {
        return (WriteSupport<T>) ProtoParquetWriter.writeSupport(descriptor);
      }
      return (WriteSupport<T>) ProtoParquetWriter.writeSupport(protoMessage);
    }
  }
}
