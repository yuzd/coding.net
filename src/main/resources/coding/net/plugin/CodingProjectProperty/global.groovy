package coding.net.plugin.CodingProjectProperty
def f = namespace(lib.FormTagLib);
/**
 * Created by Administrator on 2016/9/11 0011.
 */
f.section(title: "Coding.Net Acess Credential") {
    f.entry(title: _("coding.Login"), field: "login") {
        f.textbox()
    }

    f.entry(title: _("coding.Password"), field: "password") {
        f.password()
    }

}