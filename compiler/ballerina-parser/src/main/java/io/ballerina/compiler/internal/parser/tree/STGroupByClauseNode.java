/*
 *  Copyright (c) 2023, WSO2 LLC. (http://www.wso2.com).
 *
 *  WSO2 LLC. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied. See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package io.ballerina.compiler.internal.parser.tree;

import io.ballerina.compiler.syntax.tree.GroupByClauseNode;
import io.ballerina.compiler.syntax.tree.Node;
import io.ballerina.compiler.syntax.tree.NonTerminalNode;
import io.ballerina.compiler.syntax.tree.SyntaxKind;

import java.util.Collection;
import java.util.Collections;

/**
 * This is a generated internal syntax tree node.
 *
 * @since 2201.7.0
 */
public class STGroupByClauseNode extends STIntermediateClauseNode {
    public final STNode groupKeyword;
    public final STNode byKeyword;
    public final STNode groupingKey;

    STGroupByClauseNode(
            STNode groupKeyword,
            STNode byKeyword,
            STNode groupingKey) {
        this(
                groupKeyword,
                byKeyword,
                groupingKey,
                Collections.emptyList());
    }

    STGroupByClauseNode(
            STNode groupKeyword,
            STNode byKeyword,
            STNode groupingKey,
            Collection<STNodeDiagnostic> diagnostics) {
        super(SyntaxKind.GROUP_BY_CLAUSE, diagnostics);
        this.groupKeyword = groupKeyword;
        this.byKeyword = byKeyword;
        this.groupingKey = groupingKey;

        addChildren(
                groupKeyword,
                byKeyword,
                groupingKey);
    }

    @Override
    public STNode modifyWith(Collection<STNodeDiagnostic> diagnostics) {
        return new STGroupByClauseNode(
                this.groupKeyword,
                this.byKeyword,
                this.groupingKey,
                diagnostics);
    }

    public STGroupByClauseNode modify(
            STNode groupKeyword,
            STNode byKeyword,
            STNode groupingKey) {
        if (checkForReferenceEquality(
                groupKeyword,
                byKeyword,
                groupingKey)) {
            return this;
        }

        return new STGroupByClauseNode(
                groupKeyword,
                byKeyword,
                groupingKey,
                diagnostics);
    }

    @Override
    public Node createFacade(int position, NonTerminalNode parent) {
        return new GroupByClauseNode(this, position, parent);
    }

    @Override
    public void accept(STNodeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public <T> T apply(STNodeTransformer<T> transformer) {
        return transformer.transform(this);
    }
}
