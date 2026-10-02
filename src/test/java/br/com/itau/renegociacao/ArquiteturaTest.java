package br.com.itau.renegociacao;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

class ArquiteturaTest {

    private static final JavaClasses CLASSES = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("br.com.itau.renegociacao");

    @Test
    void dependenciasApontamSempreParaDentro() {
        layeredArchitecture()
                .consideringOnlyDependenciesInLayers()
                .layer("Domain").definedBy("..domain..")
                .layer("Application").definedBy("..application..")
                .layer("Adapter").definedBy("..adapter..")
                .layer("Infrastructure").definedBy("..infrastructure..")
                .whereLayer("Infrastructure").mayNotBeAccessedByAnyLayer()
                .whereLayer("Adapter").mayOnlyBeAccessedByLayers("Infrastructure")
                .whereLayer("Application").mayOnlyBeAccessedByLayers("Adapter", "Infrastructure")
                .whereLayer("Domain").mayOnlyBeAccessedByLayers("Application", "Adapter", "Infrastructure")
                .check(CLASSES);
    }

    @Test
    void nucleoNaoConheceFrameworks() {
        noClasses()
                .that().resideInAnyPackage("..domain..", "..application..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "org.springframework..", "com.github.benmanes..", "jakarta..", "com.fasterxml..", "lombok..")
                .check(CLASSES);
    }

    @Test
    void aplicacaoNaoDependeDosSimuladores() {
        noClasses()
                .that().resideOutsideOfPackage("..simulador..")
                .should().dependOnClassesThat().resideInAPackage("..simulador..")
                .check(CLASSES);
    }

    @Test
    void simuladoresNaoDependemDaAplicacao() {
        noClasses()
                .that().resideInAPackage("..simulador..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "..domain..", "..application..", "..adapter..", "..infrastructure..")
                .check(CLASSES);
    }
}