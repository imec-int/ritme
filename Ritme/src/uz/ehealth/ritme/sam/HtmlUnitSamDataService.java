package uz.ehealth.ritme.sam;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by bdcuyp0 on 14-6-2016.
 */
public class HtmlUnitSamDataService implements SamDataService {
    private static final Logger LOG = LoggerFactory.getLogger(HtmlUnitSamDataService.class);

    @Override
    public String getActualSamDataVersion(final String user, final String nihiiOrg) {
        WebClient webClient = new WebClient(BrowserVersion.FIREFOX_3);

        try {
            webClient.addRequestHeader("Accept-Language", "nl");
            HtmlPage versionPage = webClient.getPage("https://www.vas.ehealth.fgov.be/websamcivics/samcivics/version/lastversion.html");

            return getVersionFromPage(versionPage);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }



    @Override
    public InputStream getActualSamDataAsZipStream(final String user, final String nihiiOrg) {
        WebClient webClient = new WebClient(BrowserVersion.FIREFOX_3);

        try {
            webClient.addRequestHeader("Accept-Language", "nl");
            HtmlPage versionPage = webClient.getPage("https://www.vas.ehealth.fgov.be/websamcivics/samcivics/version/lastversion.html");

            final String versieString = getVersionFromPage(versionPage);
            Integer version;
            try {
                //het versie-nummer

                version = Integer.parseInt(versieString);
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
                return null;
            }


            //click download -> download volledig in het menu
            List anchors = versionPage.getByXPath("//ul/li[a/text()='Download']/ul/li/a[1]");
            HtmlPage page = ((HtmlAnchor) anchors.get(0)).click();

            //click op de knop van de laatste versie, en de eerste opgelijste versie is 2
            HtmlForm downloadForm = page.getFormByName("formDownload");

            final String xpathExpr = "//input[@type='submit' and @value='Download ' and contains(@name,'formDownload') and contains(@name,':" + (version - 2) + ":')]";
            List buttons = downloadForm.getByXPath(xpathExpr);
            LOG.debug("Found {} possible download buttons", buttons.size());
            //HtmlInput input = downloadForm.getInputByName("formDownload:j_idt34:" + (version - 2) + ":j_idt45");
            if (buttons.isEmpty()) {
                throw new RuntimeException("No download button found for pattern " + xpathExpr);
            }
            HtmlInput input = (HtmlInput) buttons.get(0);
            Page zipPage = input.click();

            return zipPage.getWebResponse().getContentAsStream();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }


    }

    private String getVersionFromPage(final HtmlPage versionPage) {
        List objects = versionPage.getByXPath("//div[@id=\"page\"]//text()[preceding::h1[text()='Laatste versie'] and following::br][1]");
        LOG.debug("Found {} possible version numbers", objects.size());
        return ((DomText) objects.get(0)).asText();
    }
}
