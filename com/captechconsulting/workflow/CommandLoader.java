package com.captechconsulting.workflow;

import com.google.common.collect.Maps;
import org.apache.commons.chain.Catalog;
import org.apache.commons.chain.CatalogFactory;
import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.impl.CatalogBase;
import org.apache.commons.chain.impl.ChainBase;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;

/**
 * Annotation based configuration of Apache Commons Chain.
 * Will scan for any classes annotated with @ChainedCommand and \
 * create catalogs and commands based on the attributes. Supports
 * chains as well as single commands.
 */
@Component
public class CommandLoader {

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * Scans the application context for beans annotated with ChainedCommand.
     * Can be called multiple times, as it clears the data when called.
     *
     * @throws Exception
     */
    @PostConstruct
    public void load() throws Exception {
        CatalogFactory.clear();
        Map<ChainedCommand, Object> commands = getOrderedCommands();
        for (ChainedCommand command : commands.keySet()) {
            Catalog catalog = getOrCreateCatalog(command.catalog());
            catalog.addCommand(command.value(), (Command) commands.get(command));
        }
    }

    /**
     * Iterates through the beans found in the context and prepares
     * them to be inserted into the catalog.
     * 1. Does sanity check on all beans detected.
     * 2. Adds commands that don't have a chain attribute to the result.
     * 3. Creates chains from beans that have a chain attribute in the
     *    annotation before adding the chain to the result.
     * @return a map of annotations and the command they represent
     */
    private Map<ChainedCommand, Object> getOrderedCommands() {

        // All beans with the annotation
        Map<String, Object> annotatedBeans = applicationContext.getBeansWithAnnotation(ChainedCommand.class);

        // Final results map
        Map<ChainedCommand, Object> result = Maps.newHashMap();

        // Any command that specifies a "chain"
        Map<String, SortedMap<ChainedCommand, Object>> chainsOfCommands = Maps.newHashMap();

        for (String name : annotatedBeans.keySet()) {
            Object bean = annotatedBeans.get(name);
            ChainedCommand chainedCommand = AnnotationUtils.findAnnotation(bean.getClass(), ChainedCommand.class);
            sanityCheck(chainedCommand, name, bean);
            if (StringUtils.isNotBlank(chainedCommand.chain())) {
                // The command has a chain so we add it to a separate map
                SortedMap<ChainedCommand, Object> chain = chainsOfCommands.get(chainedCommand.catalog() + ":" + chainedCommand.chain());
                if (chain == null) {
                    chain = Maps.newTreeMap(getComparator());
                    chainsOfCommands.put(chainedCommand.chain(), chain);
                }
                chain.put(chainedCommand, bean);
            } else {
                // No chain so just add it to the final results
                result.put(chainedCommand, bean);
            }
        }
        // Create "real" chain object from all the beans that specified a "chain"
        result.putAll(createChains(chainsOfCommands));

        return result;
    }

    /**
     * Basic sanity check to see that the annotations don't have a conflicting setup.
     * @param chainedCommand
     * @param name
     * @param bean
     */
    private void sanityCheck(ChainedCommand chainedCommand, String name, Object bean) {
        Assert.isTrue(
                (StringUtils.isBlank(chainedCommand.chain()) && chainedCommand.order() == 0) ||
                        (StringUtils.isNotBlank(chainedCommand.chain()) && chainedCommand.order() != 0),
                "ChainedCommand must have 'chain' and 'order' or both must be blank");
        Assert.isAssignable(Command.class, bean.getClass(),
                "Bean " + name + " does not implement the required interface: " + Command.class.getCanonicalName());
    }

    /**
     * Creates "real" chain objects out of beans that implement the
     * org.apache.commons.chain.Command interface so they can be
     * executed as a single command
     * @param chainsOfCommands
     * @return
     */
    private Map<ChainedCommand, Chain> createChains(Map<String, SortedMap<ChainedCommand, Object>> chainsOfCommands) {
        Map<ChainedCommand, Chain> result = Maps.newHashMap();
        // Get all the commands with a chain
        for (String name : chainsOfCommands.keySet()) {
            // Create a "real" chain
            ChainBase chainBase = new ChainBase();
            SortedMap<ChainedCommand, Object> chain = chainsOfCommands.get(name);
            for (ChainedCommand command : chain.keySet()) {
                chainBase.addCommand((Command) chain.get(command));
            }
            // Add the chain implementation to our result
            result.put(chain.firstKey(), chainBase);
        }
        return result;
    }

    /**
     * Get's the catalog with the given name, if it doesn't exist
     * it creates a new catalog
     * @param name
     * @return
     */
    private Catalog getOrCreateCatalog(String name) {
        Catalog catalog = CatalogFactory.getInstance().getCatalog(name);
        if (catalog != null) {
            return catalog;
        }

        Catalog newCatalog = new CatalogBase();
        CatalogFactory.getInstance().addCatalog(name, newCatalog);
        return newCatalog;
    }

    /**
     * Clears the catalog on exit
     * @throws Exception
     */
    @PreDestroy
    public void destroy() throws Exception {
        CatalogFactory.clear();
    }

    /**
     * Get's the catalog with the given name, otherwise null
     * @param name
     * @return
     */
    public Catalog getCatalog(String name) {
        return CatalogFactory.getInstance().getCatalog(name);
    }

    /**
     * Get's the catalog named "DEFAULT" which is used when no
     * catalog is specified on an annotation
     * @return
     */
    public Catalog getDefaultCatalog() {
        return CatalogFactory.getInstance().getCatalog("DEFAULT");
    }

    /**
     * Compares two annotations by looking only at the order attribute.
     * For use with ordering within a chain.
     * @return
     */
    private Comparator<ChainedCommand> getComparator() {
        return new Comparator<ChainedCommand>() {
            @Override
            public int compare(ChainedCommand o1, ChainedCommand o2) {
                return new Integer(o1.order()).compareTo(o2.order());
            }
        };
    }
}
