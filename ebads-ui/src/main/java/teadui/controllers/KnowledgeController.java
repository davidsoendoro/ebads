package teadui.controllers;

import com.davstele.models.Sequence;
import com.davstele.sequencers.ISequencer;
import com.davstele.sequencers.PcfSequencer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.repository.support.Repositories;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import teadui.Application;
import teadui.models.Knowledge;
import teadui.models.repositories.KnowledgeRepository;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dx on 5/19/16.
 */
@Controller
public class KnowledgeController {

    @Autowired
    ApplicationContext context;

    @RequestMapping("/knowledge")
    public String knowledge(@RequestParam(value="name", required=false, defaultValue="World") String name, Model model) {
        model.addAttribute("name", name);

        List<String> knowledges = new ArrayList<>();
        knowledges.add("Knowledge 1");
        knowledges.add("Knowledge 2");
        knowledges.add("Knowledge 3");
        model.addAttribute("knowledges", knowledges);
        return "knowledge";
    }

    @RequestMapping(value="/knowledge/import", method=RequestMethod.GET)
    public String getKnowledgeImport(@RequestParam(value="method", required=false, defaultValue = "GET") String method,
                                     @ModelAttribute("message") String message,
                                     @ModelAttribute("error") String error, Model model) {
        model.addAttribute("message", message);
        model.addAttribute("error", error);
        model.addAttribute("method", method);
        return "knowledge_import";
    }

    @RequestMapping(value="/knowledge/import", method=RequestMethod.POST)
    public String postKnowledgeImport(@RequestParam(value="method", required=false, defaultValue = "POST") String method,
                                      @RequestParam("file") MultipartFile file,
                                      Model model, RedirectAttributes redirectAttributes) {
        if(!file.isEmpty()) {
            String name = file.getOriginalFilename();

            try {
                // Successfully upload the file
                File localFile = new File(Application.ROOT + "/" + name);
                BufferedOutputStream stream = new BufferedOutputStream(
                        new FileOutputStream(localFile));
                FileCopyUtils.copy(file.getInputStream(), stream);
                stream.close();

                // Sequenced the file
                ISequencer sequencer = new PcfSequencer();
                sequencer.processFile(localFile);
                List<Sequence> sequences = sequencer.getSequences();

                // Insert the sequence file to database
                Repositories repositories = new Repositories(context);
                KnowledgeRepository knowledgeRepository = (KnowledgeRepository)
                        repositories.getRepositoryFor(Knowledge.class);
                List<Knowledge> knowledges = new ArrayList<>();
                for(Sequence sequence : sequences) {
                    Knowledge knowledge = new Knowledge(sequence, "");
                    knowledges.add(knowledge);
                }
                knowledgeRepository.save(knowledges);

                redirectAttributes.addFlashAttribute("message",
                        "You successfully uploaded " + name + "!");
            }
            catch (Exception e) {
                redirectAttributes.addFlashAttribute("error",
                        "You failed to upload " + name + " => " + e.getMessage());
            }
        }
        else {
            redirectAttributes.addFlashAttribute("error",
                    "Specify a file to import!");
        }

        model.addAttribute("method", method);
        return "redirect:/knowledge/import";
    }
}
