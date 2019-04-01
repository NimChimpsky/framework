package config;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Set;

public class UrlProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement typeElement : annotations) {
            Set<? extends Element> annotated = roundEnv.getElementsAnnotatedWith(typeElement);
            for (Element element : annotated) {
                System.out.println(element.getSimpleName());
            }
        }
        return true;
    }
}
