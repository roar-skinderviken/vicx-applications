export const dynamic = "force-static"

export default function PortfolioPage() {
    return (
        <main className="content">
            <div className="hero">
                <h1><span>Welcome to My Portfolio!</span></h1>
                <p className="lead">Explore my projects and experience below.</p>
            </div>

            <div className="container my-5">
                <h2 className="text-center text-3xl my-4">My Projects</h2>

                <div className="flex flex-col items-center">
                    <div className="flex flex-col space-y-4">

                        <div className="project">
                            <h2>Web Development</h2>
                            <ul>
                                <li>Passionate about crafting engaging web experiences using HTML, CSS, and
                                    JavaScript.
                                </li>
                                <li>Using GitHub repositories to push changes and sync with my server.</li>
                                <li>Specializing in responsive design and modern web technologies.</li>
                                <li>Leveraging Bootstrap to enhance and modernize website design.</li>
                                <li>Focused on achieving a clean and professional appearance while ensuring all
                                    necessary
                                    functionalities are included.
                                </li>
                            </ul>
                            <img src="/images/nginx.png" alt="NGINX"/>
                        </div>

                        <div className="project">
                            <h2>Backend Development</h2>
                            <ul>
                                <li>Developed backend systems using Java, Python and C#.</li>
                                <li>C# for simple game mechanics from Unity Development.</li>
                                <li>Python for functional and complete applications.</li>
                                <li>Java with databases and simpler programs.</li>
                                <li>Focused on creating efficient and scalable server-side applications.</li>
                            </ul>
                            <img src="/images/tom.png" alt="Backend Development"/>
                        </div>

                        <div className="project">
                            <h2>Server Management</h2>
                            <ul>
                                <li>Set up local and public servers using Node.js and NGINX.</li>
                                <li>Setting up domain certificates, such as vicx.no, using Certbot.</li>
                                <li>Setting up Kubernetes with MicroK8s for easy container management and streamlined
                                    operations.
                                </li>
                            </ul>
                            <img src="/images/node.png" alt="Node.js"/>
                        </div>

                        <div className="project">
                            <h2>Raspberry Pi Server</h2>
                            <ul>
                                <li>Operating a Raspberry Pi server with Raspberry Pi Lite x64 OS.</li>
                                <li>Customizing Kali Linux with multiple desktop environments, including KDE Plasma and
                                    GNOME.
                                </li>
                                <li>Setting up a fully functional Ubuntu server with SSH access configured.</li>
                                <li>Currently running a cluster with two Raspberry Pi devices, using Ubuntu Server, with
                                    one
                                    acting as
                                    the master node.
                                </li>
                            </ul>
                            <img src="/images/rasp.png" alt="Raspberry Pi"/>
                        </div>

                        <div className="project">
                            <h2>Linux</h2>
                            <ul>
                                <li>Customizing and optimizing my PC system with Arch Linux with and without the Black
                                    Arch
                                    tools.
                                </li>
                                <li>Exploring different desktop environments and tools for performance tuning.</li>
                                <li>Also familiar with distributions such as Black Arch, Fedora, Ubuntu, Ubuntu Server,
                                    Kali,
                                    ParrotOS &
                                    Debian GNU/Linux.
                                </li>
                            </ul>
                            <img src="/images/Tux.png" alt="Tux Penguin"/>
                        </div>

                        <div className="project">
                            <h2>Packets</h2>
                            <ul>
                                <li>Packetmanagers used: Pacman, Yay, Flatpak, Snap, Brew, DNF and Apt.</li>
                                <li>Text editors used: Vim, Vi and Nano.</li>
                                <li>Desktops used: KDE Plasma 6, GNOME, Hyprland.</li>
                                <li>Consoles used: Konsole, Kitty, GNOME Terminal and MATE Terminal.</li>
                            </ul>
                            <img src="/images/arch.png" alt="arch"/>
                        </div>

                        <div className="project">
                            <h2>Database Management</h2>
                            <ul>
                                <li>Proficient in database management with MySQL, H2 and SQLite3.</li>
                                <li>Designing efficient schemas and optimizing query performance.</li>
                            </ul>
                            <img src="/images/mysql.png" alt="MySQL"/>
                        </div>
                    </div>
                </div>
            </div>
        </main>
    )
}