/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

namespace Apache.Ignite.Core.Cache.Query
{
    using System;
    using System.Diagnostics.CodeAnalysis;
    using Apache.Ignite.Core.Impl.Binary;
    using Apache.Ignite.Core.Impl.Cache;

    /// <summary>
    /// SQL Query.
    /// </summary>
    public class SqlQuery : QueryBase
    {
        /// <summary>
        /// Constructor.
        /// </summary>
        /// <param name="queryType">Type.</param>
        /// <param name="sql">SQL.</param>
        /// <param name="args">Arguments.</param>
        public SqlQuery(Type queryType, string sql, params object[] args) : this(queryType, sql, false, args)
        {
            // No-op.
        }

        /// <summary>
        /// Constructor.
        /// </summary>
        /// <param name="queryType">Type.</param>
        /// <param name="sql">SQL.</param>
        /// <param name="local">Whether query should be executed locally.</param>
        /// <param name="args">Arguments.</param>
        public SqlQuery(Type queryType, string sql, bool local, params object[] args) 
            : this(queryType.Name, sql, local, args)
        {
            // No-op.
        }
        
        /// <summary>
        /// Constructor.
        /// </summary>
        /// <param name="queryType">Type.</param>
        /// <param name="sql">SQL.</param>
        /// <param name="args">Arguments.</param>
        public SqlQuery(string queryType, string sql, params object[] args) : this(queryType, sql, false, args)
        {
            // No-op.
        }

        /// <summary>
        /// Constructor.
        /// </summary>
        /// <param name="queryType">Type.</param>
        /// <param name="sql">SQL.</param>
        /// <param name="local">Whether query should be executed locally.</param>
        /// <param name="args">Arguments.</param>
        public SqlQuery(string queryType, string sql, bool local, params object[] args)
        {
            QueryType = queryType;
            Sql = sql;
            Local = local;
            Arguments = args;
        }

        /// <summary>
        /// Type.
        /// </summary>
        public string QueryType { get; set; }

        /// <summary>
        /// SQL.
        /// </summary>
        public string Sql { get; set; }

        /// <summary>
        /// Arguments.
        /// </summary>
        [SuppressMessage("Microsoft.Performance", "CA1819:PropertiesShouldNotReturnArrays")]
        public object[] Arguments { get; set; }

        /** <inheritDoc /> */
        internal override void Write(BinaryWriter writer, bool keepBinary)
        {
            if (string.IsNullOrEmpty(Sql))
                throw new ArgumentException("Sql cannot be null or empty");

            if (string.IsNullOrEmpty(QueryType))
                throw new ArgumentException("QueryType cannot be null or empty");

            // 2. Prepare.
            writer.WriteBoolean(Local);
            writer.WriteString(Sql);
            writer.WriteString(QueryType);
            writer.WriteInt(PageSize);

            WriteQueryArgs(writer, Arguments);
        }

        /** <inheritDoc /> */
        internal override CacheOp OpId
        {
            get { return CacheOp.QrySql; }
        }
    }
}
