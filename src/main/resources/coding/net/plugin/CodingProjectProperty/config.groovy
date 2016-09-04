package coding.net.plugin.CodingProjectProperty

import static coding.net.plugin.CodingProjectProperty.DescriptorImpl.CODING_PROJECT_BLOCK_NAME

def f = namespace(lib.FormTagLib);

f.optionalBlock(name: CODING_PROJECT_BLOCK_NAME, title: _('coding.project'), checked: instance != null) {
    f.entry(field: 'projectUrlStr', title: _('coding.project.url')) {
        f.textbox()
    }

    f.advanced() {
        f.entry(title: _('coding.build.display.name'), field: 'displayName') {
            f.textbox()
        }
    }
}
