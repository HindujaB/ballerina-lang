/*
 *  Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package io.ballerina.projects.test;

import io.ballerina.projects.DependencyGraph;
import io.ballerina.projects.Document;
import io.ballerina.projects.DocumentId;
import io.ballerina.projects.EmitResult;
import io.ballerina.projects.JBallerinaBackend;
import io.ballerina.projects.JvmTarget;
import io.ballerina.projects.Module;
import io.ballerina.projects.ModuleId;
import io.ballerina.projects.ModuleMd;
import io.ballerina.projects.Package;
import io.ballerina.projects.PackageCompilation;
import io.ballerina.projects.PackageMd;
import io.ballerina.projects.PackageResolution;
import io.ballerina.projects.ProjectEnvironmentBuilder;
import io.ballerina.projects.ResolvedPackageDependency;
import io.ballerina.projects.bala.BalaProject;
import io.ballerina.projects.directory.BuildProject;
import io.ballerina.projects.internal.model.Target;
import io.ballerina.projects.repos.TempDirCompilationCache;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

/**
 * Contains cases to test the load bala project.
 *
 * @since 2.0.0
 */
public class TestBalaProject {

    private static final Path RESOURCE_DIRECTORY = Paths.get("src", "test", "resources");

    @Test(description = "tests loading a valid bala project")
    public void testBalaProjectAPI() {
        Path balaPath = RESOURCE_DIRECTORY.resolve("balaloader").resolve("foo-winery-any-0.1.0.bala");
        // 1) Initialize the project instance
        BalaProject balaProject = null;
        try {
            ProjectEnvironmentBuilder defaultBuilder = ProjectEnvironmentBuilder.getDefaultBuilder();
            defaultBuilder.addCompilationCacheFactory(TempDirCompilationCache::from);
            balaProject = BalaProject.loadProject(defaultBuilder, balaPath);
        } catch (Exception e) {
            Assert.fail(e.getMessage(), e);
        }
        // 2) Load the package
        Package currentPackage = balaProject.currentPackage();
        // 3) Load the default module
        Module defaultModule = currentPackage.getDefaultModule();
        Assert.assertEquals(defaultModule.moduleName().toString(), "winery");
        Assert.assertEquals(defaultModule.documentIds().size(), 2);

        // TODO find an easy way to test the project structure. e.g. serialize the structure in a json file.
        int noOfSrcDocuments = 0;
        int noOfTestDocuments = 0;
        final ArrayList<String> moduleNames = new ArrayList<>(
                Arrays.asList("winery.services", "winery.storage", "winery"));
        final Collection<ModuleId> moduleIds = currentPackage.moduleIds();
        Assert.assertEquals(moduleIds.size(), 3);

        for (ModuleId moduleId : moduleIds) {
            Module module = currentPackage.module(moduleId);
            // test module names
            if (!moduleNames.contains(module.moduleName().toString())) {
                Assert.fail("module name '" + module.moduleName().toString() + "' is not valid");
            }
            for (DocumentId documentId : module.documentIds()) {
                noOfSrcDocuments++;
            }
            for (DocumentId testDocumentId : module.testDocumentIds()) {
                noOfTestDocuments++;
            }
            for (Document doc : module.documents()) {
                Assert.assertNotNull(doc.syntaxTree());
            }
        }

        Assert.assertEquals(noOfSrcDocuments, 4);
        Assert.assertEquals(noOfTestDocuments, 0);

        PackageResolution resolution = currentPackage.getResolution();
        DependencyGraph<ResolvedPackageDependency> packageDescriptorDependencyGraph = resolution.dependencyGraph();
        Assert.assertEquals(packageDescriptorDependencyGraph.getNodes().size(), 1);
        DependencyGraph<ModuleId> moduleIdDependencyGraph = currentPackage.moduleDependencyGraph();
        Assert.assertEquals(moduleIdDependencyGraph.getNodes().size(), 3);
    }

    @Test (description = "test balo project load with newly created balo")
    public void testBaloProjectAPIWithNewBaloBuild() throws IOException {
        Path projectPath = RESOURCE_DIRECTORY.resolve("myproject");

        // 1) Initialize the project instance
        BuildProject project = null;
        try {
            project = BuildProject.load(projectPath);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }

        PackageCompilation packageCompilation = project.currentPackage().getCompilation();
        if (packageCompilation.diagnosticResult().hasErrors()) {
            Assert.fail("compilation failed:" + packageCompilation.diagnosticResult().errors());
        }

        Target target = new Target(project.sourceRoot());
        Path baloPath = target.getBalaPath();
        // invoke write balo method
        JBallerinaBackend jBallerinaBackend = JBallerinaBackend.from(packageCompilation, JvmTarget.JAVA_11);
        EmitResult emitResult = jBallerinaBackend.emit(JBallerinaBackend.OutputType.BALA, baloPath);

        // Load the balo as a project
        BalaProject baloProject = null;
        try {
            ProjectEnvironmentBuilder defaultBuilder = ProjectEnvironmentBuilder.getDefaultBuilder();
            defaultBuilder.addCompilationCacheFactory(TempDirCompilationCache::from);
            baloProject = BalaProject.loadProject(defaultBuilder, emitResult.generatedArtifactPath());
        } catch (Exception e) {
            Assert.fail(e.getMessage(), e);
        }

        Package currentPackage = baloProject.currentPackage();
        // Get the Package.md
        Optional<PackageMd> packageMd = currentPackage.packageMd();
        Assert.assertTrue(packageMd.isPresent());
        Assert.assertEquals(packageMd.get().content(), "# Package Md");
        // Get the Module.md
        Optional<ModuleMd> mdDocument = baloProject.currentPackage().getDefaultModule().moduleMd();
        Assert.assertTrue(mdDocument.isPresent());
        Assert.assertEquals(mdDocument.get().content(), "# Default Module Md");
    }
}
