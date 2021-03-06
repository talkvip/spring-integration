/*
 * Copyright 2002-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.integration.file.config;

import org.junit.Test;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.integration.file.entries.*;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

/**
 * @author Mark Fisher
 */
public class FileListFilterFactoryBeanTests {

    @Test(expected = IllegalArgumentException.class)
    public void customFilterAndFilenamePatternAreMutuallyExclusive() throws Exception {
        FileListFilterFactoryBean factory = new FileListFilterFactoryBean();
        factory.setFilterReference(new TestFilter());
        factory.setFilenamePattern(Pattern.compile("foo"));
        factory.getObject();
    }

    @Test
    public void customFilterAndPreventDuplicatesNull() throws Exception {
        FileListFilterFactoryBean factory = new FileListFilterFactoryBean();
        TestFilter testFilter = new TestFilter();
        factory.setFilterReference(testFilter);
        EntryListFilter<File> result = factory.getObject();
        assertFalse(result instanceof CompositeEntryListFilter);
        assertSame(testFilter, result);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void customFilterAndPreventDuplicatesTrue() throws Exception {
        FileListFilterFactoryBean factory = new FileListFilterFactoryBean();
        TestFilter testFilter = new TestFilter();
        factory.setFilterReference(testFilter);
        factory.setPreventDuplicates(Boolean.TRUE);
        EntryListFilter<File> result = factory.getObject();
        assertTrue(result instanceof CompositeEntryListFilter);
        Collection filters = (Collection) new DirectFieldAccessor(result).getPropertyValue("fileFilters");
        assertTrue(filters.iterator().next() instanceof AcceptOnceEntryFileListFilter);
        assertTrue(filters.contains(testFilter));
    }

    @Test
    public void customFilterAndPreventDuplicatesFalse() throws Exception {
        FileListFilterFactoryBean factory = new FileListFilterFactoryBean();
        TestFilter testFilter = new TestFilter();
        factory.setFilterReference(testFilter);
        factory.setPreventDuplicates(Boolean.FALSE);
        EntryListFilter<File> result = factory.getObject();
        assertFalse(result instanceof CompositeEntryListFilter);
        assertSame(testFilter, result);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void filenamePatternAndPreventDuplicatesNull() throws Exception {
        FileListFilterFactoryBean factory = new FileListFilterFactoryBean();
        factory.setFilenamePattern(Pattern.compile("foo"));
        EntryListFilter<File> result = factory.getObject();
        assertTrue(result instanceof CompositeEntryListFilter);
        Collection filters = (Collection) new DirectFieldAccessor(result).getPropertyValue("fileFilters");
        Iterator<EntryListFilter> iterator = filters.iterator();
        assertTrue(iterator.next() instanceof AcceptOnceEntryFileListFilter);
        assertTrue(iterator.next() instanceof PatternMatchingEntryListFilter);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void filenamePatternAndPreventDuplicatesTrue() throws Exception {
        FileListFilterFactoryBean factory = new FileListFilterFactoryBean();
        factory.setFilenamePattern(Pattern.compile("foo"));
        factory.setPreventDuplicates(Boolean.TRUE);
        EntryListFilter<File> result = factory.getObject();
        assertTrue(result instanceof CompositeEntryListFilter);
        Collection filters = (Collection) new DirectFieldAccessor(result).getPropertyValue("fileFilters");
        Iterator<EntryListFilter> iterator = filters.iterator();
        assertTrue(iterator.next() instanceof AcceptOnceEntryFileListFilter);
        assertTrue(iterator.next() instanceof PatternMatchingEntryListFilter);
    }

    @Test
    public void filenamePatternAndPreventDuplicatesFalse() throws Exception {
        FileListFilterFactoryBean factory = new FileListFilterFactoryBean();
        factory.setFilenamePattern(Pattern.compile("foo"));
        factory.setPreventDuplicates(Boolean.FALSE);
        EntryListFilter<File> result = factory.getObject();
        assertFalse(result instanceof CompositeEntryListFilter);
        assertTrue(result instanceof PatternMatchingEntryListFilter);
    }


    private static class TestFilter extends AbstractEntryListFilter<File> {
        @Override
        public boolean accept(File file) {
            return true;
        }
    }

}
