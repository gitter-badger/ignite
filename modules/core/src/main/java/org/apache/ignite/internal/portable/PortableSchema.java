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

package org.apache.ignite.internal.portable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Schema describing portable object content. We rely on the following assumptions:
 * - When amount of fields in the object is low, it is better to inline these values into int fields thus allowing
 * for quick comparisons performed within already fetched L1 cache line.
 * - When there are more fields, we store them inside a hash map.
 */
public class PortableSchema implements Serializable {
    /** */
    private static final long serialVersionUID = 0L;

    /** Order returned if field is not found. */
    public static final int ORDER_NOT_FOUND = -1;

    /** Inline flag. */
    private final boolean inline;

    /** Map with offsets. */
    private final HashMap<Integer, Integer> map;

    /** ID 1. */
    private final int id0;

    /** ID 2. */
    private final int id1;

    /** ID 3. */
    private final int id2;

    /** ID 4. */
    private final int id3;

    /** ID 1. */
    private final int id4;

    /** ID 2. */
    private final int id5;

    /** ID 3. */
    private final int id6;

    /** ID 4. */
    private final int id7;

    /** Schema ID. */
    private final int schemaId;

    /**
     * Constructor.
     *
     * @param schemaId Schema ID.
     * @param fieldIds Field IDs.
     */
    private PortableSchema(int schemaId, List<Integer> fieldIds) {
        this.schemaId = schemaId;

        if (fieldIds.size() <= 8) {
            inline = true;

            Iterator<Integer> iter = fieldIds.iterator();

            id0 = iter.hasNext() ? iter.next() : 0;
            id1 = iter.hasNext() ? iter.next() : 0;
            id2 = iter.hasNext() ? iter.next() : 0;
            id3 = iter.hasNext() ? iter.next() : 0;
            id4 = iter.hasNext() ? iter.next() : 0;
            id5 = iter.hasNext() ? iter.next() : 0;
            id6 = iter.hasNext() ? iter.next() : 0;
            id7 = iter.hasNext() ? iter.next() : 0;

            map = null;
        }
        else {
            inline = false;

            id0 = id1 = id2 = id3 = id4 = id5 = id6 = id7 = 0;

            map = new HashMap<>();

            for (int i = 0; i < fieldIds.size(); i++)
                map.put(fieldIds.get(i), i);
        }
    }

    /**
     * @return Schema ID.
     */
    public int schemaId() {
        return schemaId;
    }

    /**
     * Get field position in footer by schema ID.
     *
     * @param id Field ID.
     * @return Offset or {@code 0} if there is no such field.
     */
    public int order(int id) {
        if (inline) {
            if (id == id0)
                return 0;

            if (id == id1)
                return 1;

            if (id == id2)
                return 2;

            if (id == id3)
                return 3;

            if (id == id4)
                return 4;

            if (id == id5)
                return 5;

            if (id == id6)
                return 6;

            if (id == id7)
                return 7;

            return ORDER_NOT_FOUND;
        }
        else {
            Integer order = map.get(id);

            return order != null ? order : ORDER_NOT_FOUND;
        }
    }

    /**
     * Schema builder.
     */
    public static class Builder {
        /** Schema ID. */
        private int schemaId = PortableUtils.schemaInitialId();

        /** Fields. */
        private final ArrayList<Integer> fields = new ArrayList<>();

        /**
         * Create new schema builder.
         *
         * @return Schema builder.
         */
        public static Builder newBuilder() {
            return new Builder();
        }

        /**
         * Private constructor.
         */
        private Builder() {
            // No-op.
        }

        /**
         * Add field.
         *
         * @param fieldId Field ID.
         */
        public void addField(int fieldId) {
            fields.add(fieldId);

            schemaId = PortableUtils.updateSchemaId(schemaId, fieldId);
        }

        /**
         * Build schema.
         *
         * @return Schema.
         */
        public PortableSchema build() {
            return new PortableSchema(schemaId, fields);
        }
    }
}
