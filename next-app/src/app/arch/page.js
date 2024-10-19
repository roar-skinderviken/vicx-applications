import {urlFromBasePath} from "@/app/basePathUtils"
import Hero from "@/components/Hero"

export const metadata = {
    title: "Arch | VICX"
}

export default function ArchPage() {
    return (
        <main className="content">
            <Hero
                title="Arch Linux Setup"
                lead="Learn how to set up Arch Linux on your system."
            />

            <div className="container my-5 px-2 mx-auto">
                <h2 className="text-center text-3xl my-4">Arch Linux Installation Guide</h2>
                <div className="flex justify-center">
                    <div className="w-full max-w-2xl">
                        <div className="my-4">
                            <h3 className="text-left text-2xl font-semibold mb-4">Step 1: Prepare Installation
                                Media</h3>
                            <img src={urlFromBasePath("/images/blackarch.png")} alt="Prepare Installation Media"
                                 className="w-full max-w-xs mb-3 mx-auto"/>
                            <p>Download the Arch Linux ISO and create a bootable USB drive.</p>
                            <ul className="list-disc ml-5">
                                <li>Download the Arch Linux ISO from the official website.</li>
                                <li>Use a tool like Balena Etcher to create a bootable USB drive.</li>
                                <li>Ensure the USB drive is properly formatted and the ISO is correctly written.</li>
                                <li>Ensure to resize your disk to allocate the desired amount of GB for your system.
                                </li>
                            </ul>
                        </div>
                        <div className="mb-4">
                            <h3 className="text-left text-2xl font-semibold mb-4">Step 2: Boot and Initial Setup</h3>
                            <img src={urlFromBasePath("/images/boot.png")} alt="Boot and Initial Setup"
                                 className="w-full max-w-xl mb-3 mx-auto"/>
                            <p>Boot from the USB drive and start the Arch Linux installation process.</p>
                            <ol className="list-decimal ml-5">
                                <li>Before you boot, ensure that secure boot is disabled.</li>
                                <li>Insert the USB drive into your computer and boot from it.</li>
                                <li>Select the Arch Linux installation option from the boot menu.</li>
                                <li>Set the keyboard layout and verify network connectivity. In my case <code>loadkeys
                                    no</code></li>
                                <li>Also up the font with <code>setfont ter-132b</code></li>
                            </ol>
                        </div>
                        <div className="mb-4">
                            <h3 className="text-left text-2xl font-semibold mb-4">Step 3: Connecting to the
                                internet</h3>
                            <img src={urlFromBasePath("/images/install.png")} alt="Connecting to the internet"
                                 className="w-full max-w-xl mb-3 mx-auto"/>
                            <p>Connect to the Internet.</p>
                            <ol className="list-decimal ml-5">
                                <li>Ping a site, e.g. <code>ping vicx.no</code>.</li>
                                <li>If you have an Ethernet connection, you'll get a response. Otherwise, you'll have to
                                    set up a WLAN connection.
                                </li>
                                <li>Connect via the <code>iwctl</code> command.</li>
                                <li>Now run <code>device list</code> from iwd.</li>
                                <li>Scan the networks with <code>station your_wlan scan</code> <span
                                    className="text-gray-500">#Note this command will not output anything.</span></li>
                            </ol>
                        </div>
                        <div className="mb-4">
                            <h3 className="text-left text-2xl font-semibold mb-4">Step 4: Partition the Disks</h3>
                            <img src={urlFromBasePath("/images/cfdisk.png")} alt="Partition the Disks"
                                 className="w-full max-w-xl mb-3 mx-auto"/>
                            <p>Use tools like <code>cfdisk</code> to partition your disk, normally referred to as sda.
                            </p>
                            <ul className="list-disc ml-5">
                                <li>Create the necessary partitions (EFI system partition, Linux x86-64 root, Linux
                                    swap).
                                </li>
                                <li>Enter <code>cfdisk</code> and allocate at least the following: 1GB for boot, 4GB for
                                    swap and the rest to your root (32GB is the minimal recommendation).
                                </li>
                                <li>Lets confirm the disks with the command <code>lsblk</code> to list your disks.</li>
                                <li>To proceed, we are going to format our disks manually like this:</li>
                                <li>For the root partition: <code>mkfs.ext4 /dev/root_partition</code></li>
                                <li>For the swap partition: <code>mkswap /dev/swap_partition</code></li>
                                <li>For the EFI partition: <code>mkfs.fat -F 32 /dev/efi_system_partition</code></li>
                                <li>To mount the file system, we will do the following:</li>
                                <li>For the root partition: <code>mount /dev/root_partition /mnt</code></li>
                                <li>For the EFI partition: <code>mount --mkdir /dev/efi_system_partition
                                    /mnt/boot</code></li>
                                <li>For the swap partition: <code>swapon /dev/swap_partition</code></li>
                                <li>We are now done with the hard part so the fun can begin!</li>
                            </ul>
                        </div>
                        <div className="mb-4">
                            <h3 className="text-left text-2xl font-semibold mb-4">Step 5: Install the Base System and
                                Configure Chroot</h3>
                            <img src={urlFromBasePath("/images/pacstrap.png")} alt="Install the Base System"
                                 className="w-full max-w-xl mb-3 mx-auto"/>
                            <p>Install the base packages for Arch Linux.</p>
                            <ul className="list-disc ml-5">
                                <li>Use the pacstrap command to install base packages: <code>pacstrap -K /mnt base linux
                                    linux-firmware nano</code>.
                                </li>
                                <li>Generate an fstab file: <code>genfstab -U /mnt &gt;&gt; /mnt/etc/fstab</code>.</li>
                                <li>Chroot into the new system: <code>arch-chroot /mnt</code>.</li>
                                <li>Note that in this tutorial, I won't go over any time or localization configs.</li>
                            </ul>
                        </div>
                        <div className="mb-4">
                            <h3 className="text-left text-2xl font-semibold mb-4">Step 6: Users and hosts</h3>
                            <img src={urlFromBasePath("/images/user.png")} alt="Configure the System"
                                 className="w-full max-w-xl mb-3 mx-auto"/>
                            <p>Lets make a user.</p>
                            <ul className="list-disc ml-5">
                                <li>Create <code>/etc/hostname</code> and add your hostname.</li>
                                <li>Now we can write <code>passwd</code> to create the root password.</li>
                                <li>Make sure to add a user if you want to be able to log in on your desktop later
                                    on: <code>useradd -m -G wheel -s /bin/bash your_user_name</code></li>
                                <li>Lets make a password for the user just added: <code>passwd your_user_name</code>
                                </li>
                                <li>If you're in your profile, go out with the following command: <code>exit</code>.
                                </li>
                                <li>Update your system: <code>pacman -Syu</code>, then download sudo: <code>pacman -S
                                    sudo</code>.
                                </li>
                                <li>Now that we have sudo installed, it's time to give privileges to our user: <code>EDITOR=nano
                                    visudo</code>.
                                </li>
                                <li>Here we will uncomment the line <code>%wheel ALL=(ALL) NOPASSWD: ALL</code> by
                                    removing the #, and leaving by pressing ctrl + x, hitting y for yes and pressing
                                    enter.
                                </li>
                            </ul>
                        </div>
                        <div className="mb-4">
                            <h3 className="text-left text-2xl font-semibold mb-4">Step 7: Install Bootloader</h3>
                            <img src={urlFromBasePath("/images/grub.png")} alt="Install Bootloader"
                                 className="w-full max-w-xl mb-3 mx-auto"/>
                            <p>Install and configure the GRUB bootloader.</p>
                            <ul className="list-disc ml-5">
                                <li>Install GRUB and related packages: <code>pacman -S grub</code>.</li>
                                <li>Install GRUB to the disk: <code>grub-install
                                    /dev/your_disk_without_any_numbers</code>.
                                </li>
                                <li>Generate the GRUB configuration file: <code>grub-mkconfig -o
                                    /boot/grub/grub.cfg</code>.
                                </li>
                                <li>Now exit with <code>exit</code> and unmount everything with <code>umount -a</code>.
                                </li>
                                <li>You can now reboot your system with the following command: <code>reboot</code>.</li>
                                <li>Remove the stick and let the machine boot into GRUB where you launch into Arch
                                    Linux.
                                </li>
                                <li>Login to your user and confirm your internet connection by pinging, for
                                    example: <code>ping vicx.no</code></li>
                            </ul>
                        </div>
                        <div className="mb-4">
                            <h3 className="text-left text-2xl font-semibold mb-4">Step 8: Desktop Environment Setup</h3>
                            <img src={urlFromBasePath("/images/neofetch.png")} alt="Reboot and Post-Installation"
                                 className="img-fluid mb-3 w-full max-w-2xl mx-auto"/>
                            <p>In this instance we will be using KDE plasma.</p>
                            <ul className="list-disc ml-5">
                                <li>Download the necessary packages as well as SDDM: <code>sudo pacman -S plasma
                                    sddm</code>.
                                </li>
                                <li>Press enter and <code>y</code> for yes on everything, as you need all the packages.
                                </li>
                                <li>Grab the packages you need with Pacman, but make sure to add Konsole and Kate.
                                    Spaces between means a new package: <code>sudo pacman -S konsole kate firefox</code>.
                                </li>
                                <li>Let's enable SDDM: <code>sudo systemctl enable sddm</code>, and launch into
                                    it: <code>sudo systemctl enable --now sddm</code>.
                                </li>
                                <li>Log in and press "Ctrl + Alt + T" to open the terminal, then write: <code>sudo
                                    pacman -S neofetch; neofetch</code>.
                                </li>
                                <li>Congrats on using Arch! You are now an Arch elitist. Any questions? Read the
                                    manual.
                                </li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
        </main>
    )
}
