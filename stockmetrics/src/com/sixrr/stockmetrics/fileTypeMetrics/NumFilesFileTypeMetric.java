/*
 * Copyright 2005-2016 Sixth and Red River Software, Bas Leijdekkers
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.sixrr.stockmetrics.fileTypeMetrics;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import com.sixrr.metrics.*;
import com.sixrr.stockmetrics.execution.BaseMetricsCalculator;
import com.sixrr.stockmetrics.i18n.StockMetricsBundle;
import gnu.trove.TObjectIntHashMap;
import gnu.trove.TObjectIntProcedure;
import org.jetbrains.annotations.Nullable;

/**
 * @author Bas Leijdekkers
 */
public class NumFilesFileTypeMetric extends FileTypeMetric {

    @Override
    public String getDisplayName() {
        return StockMetricsBundle.message("number.of.files.display.name");
    }

    @Override
    public String getAbbreviation() {
        return StockMetricsBundle.message("number.of.files.abbreviation");
    }

    @Override
    public MetricType getType() {
        return MetricType.Count;
    }

    @Nullable
    @Override
    public MetricCalculator createCalculator() {
        return new NumFilesFileTypeCalculator();
    }

    private class NumFilesFileTypeCalculator extends BaseMetricsCalculator {

        private final TObjectIntHashMap<FileType> countMap = new TObjectIntHashMap<FileType>();

        @Override
        public void endMetricsRun() {
            countMap.forEachEntry(new TObjectIntProcedure<FileType>() {
                @Override
                public boolean execute(FileType measured, int value) {
                    resultsHolder.postFileTypeMetric(NumFilesFileTypeMetric.this, measured, (double) value);
                    return true;
                }
            });
        }

        @Override
        protected PsiElementVisitor createVisitor() {
            return new Visitor();
        }

        private class Visitor extends PsiElementVisitor {

            @Override
            public void visitFile(PsiFile file) {
                super.visitFile(file);
                final FileType fileType = file.getFileType();
                if (countMap.containsKey(fileType)) {
                    countMap.increment(fileType);
                }
                else {
                    countMap.put(fileType, 1);
                }
            }
        }
    }
}