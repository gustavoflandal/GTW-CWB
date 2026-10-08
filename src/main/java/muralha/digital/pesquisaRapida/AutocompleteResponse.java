package muralha.digital.pesquisaRapida;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "autocompleteResponse")
public class AutocompleteResponse {

    private boolean success;
    private List<String> suggestions;

    public AutocompleteResponse() {
    }

    public AutocompleteResponse(boolean success, List<String> suggestions) {
        this.success = success;
        this.suggestions = suggestions;
    }

    @XmlElement(name = "sucesso")
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    @XmlElementWrapper(name = "sugestoes")
    @XmlElement(name = "item")
    public List<String> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions;
    }
}