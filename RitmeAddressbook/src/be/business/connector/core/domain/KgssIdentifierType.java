/**
 * Copyright (C) 2010 Recip-e
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package be.business.connector.core.domain;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public enum KgssIdentifierType {
	CBE("CBE"),
	SSIN("SSIN"),
	NIHII("NIHII"),
	NIHII_PHARMACY("NIHII-PHARMACY"),
	NIHII_HOSPITAL("NIHII-HOSPITAL");

	private String name;
	private static Map<String, KgssIdentifierType> lookup;

	static {
		lookup = new HashMap();
		Iterator var1 = EnumSet.allOf(KgssIdentifierType.class).iterator();

		while(var1.hasNext()) {
			KgssIdentifierType type = (KgssIdentifierType)var1.next();
			lookup.put(type.getName(), type);
		}

	}

	private KgssIdentifierType(String name) {
		this.name = name;
	}

	public static KgssIdentifierType lookup(String name) {
		return lookup.containsKey(name)?(KgssIdentifierType)lookup.get(name):null;
	}

	public String getName() {
		return this.name;
	}
}
